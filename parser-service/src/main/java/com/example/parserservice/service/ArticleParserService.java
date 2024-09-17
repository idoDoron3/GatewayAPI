package com.example.parserservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.parserservice.DTO.ParsedWordMappingDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.concurrent.*;
@Service
public class ArticleParserService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newCachedThreadPool();  // Dynamic thread pool

    @Value("${dal.service.url}")
    private String articleServiceUrl;
    // Minimum chunk size to justify using multiple threads
    private static final int MIN_CHUNK_SIZE = 500;

    public ArticleParserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //parse the article to map each word's offset using concurrency if applicable
    public List<ParsedWordMappingDTO> parseArticle(String content, Long articleId) throws InterruptedException, ExecutionException {
        int contentLength = content.length();
        // If the content is small, process in a single thread
        if (contentLength < MIN_CHUNK_SIZE) {
            return parseSingleThread(content, articleId);
        }
        int numThreads = Math.min(Runtime.getRuntime().availableProcessors(), contentLength / MIN_CHUNK_SIZE);
        int chunkSize = contentLength / numThreads;
        List<Future<Map<String, List<Integer>>>> futures = new ArrayList<>();

        // parse each task different thread
        for (int i = 0; i < numThreads; i++) {
            final int start = adjustChunkStart(content, i * chunkSize);
            final int end;
            if(i== numThreads-1) {
                end = contentLength;
            }
            else{
                end = adjustChunkEnd(content, (i+1)*chunkSize);
            }
            String chunk = content.substring(start, end);
            futures.add(executorService.submit(() -> parseChunk(chunk, start)));
        }

        // Merge to one map
        Map<String, List<Integer>> wordMappings = new HashMap<>();
        //for eah future - from each task
        for (Future<Map<String, List<Integer>>> future : futures) {
            Map<String, List<Integer>> result = future.get();
            // iterate this future map words and add it to the big wordsmapping
            for (Map.Entry<String, List<Integer>> entry : result.entrySet()) {
                String word = entry.getKey();
                List<Integer> offsets = entry.getValue();
                wordMappings.putIfAbsent(word, new ArrayList<>());
                wordMappings.get(word).addAll(offsets);
            }
        }
        //convert the map value from List<Integer> to string - to send ath DTO to the dal
        List<ParsedWordMappingDTO> result = convertToDTO(wordMappings, articleId);
        // Send parsed word mappings to the DAL service
        sendParsedDataToDal(result);
        return result;
    }

    // Adjust chunk end to ensure no word is split
    private int adjustChunkEnd(String content, int chunkEnd) {
        // Edge case: If we're at the end of the content, return the full length
        if (chunkEnd >= content.length()) {
            return content.length();
        }
        while (chunkEnd > 0 && content.charAt(chunkEnd) != ' ') {
            chunkEnd--;
        }
        return chunkEnd;
    }

    private int adjustChunkStart(String content, int chunkStart) {
        while (chunkStart < content.length() && content.charAt(chunkStart) != ' ') {
            chunkStart++;
        }
        return chunkStart + 1;  // Move past the space
    }

    // Parse in a single thread for small content
    private List<ParsedWordMappingDTO> parseSingleThread(String content, Long articleId) {
        Map<String, List<Integer>> wordMappings = parseChunk(content, 0);
        // Convert map to list of ParsedWordMappingDTO objects
        List<ParsedWordMappingDTO> result = convertToDTO(wordMappings, articleId);
        sendParsedDataToDal(result);
        return result;
    }

    // Parse a chunk of the article content and return word mappings
    private Map<String, List<Integer>> parseChunk(String content, int offsetBase) {
        Map<String, List<Integer>> wordMappings = new HashMap<>();
        StringBuilder currentWord = new StringBuilder();
        int wordStart = -1;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (Character.isLetter(c)) {
                if (wordStart == -1) {
                    wordStart = i;
                }
                currentWord.append(c); // Append the character as-is (no lowercase conversion yet)
            } else {
                if (currentWord.length() > 0) {
                    addWordToMappings(wordMappings, currentWord.toString(), wordStart + offsetBase);
                    currentWord.setLength(0);
                    wordStart = -1;
                }
            }
        }
        //if last word ends with letter
        if (currentWord.length() > 0) {
            addWordToMappings(wordMappings, currentWord.toString(), wordStart + offsetBase);
        }

        return wordMappings;
    }

    //add word to wordMappings
    private void addWordToMappings(Map<String, List<Integer>> wordMappings, String word, int offset) {
        word = convertToLower(word); // Convert to lowercase
        if (!wordMappings.containsKey(word)) {
            wordMappings.put(word, new ArrayList<>());
        }
        wordMappings.get(word).add(offset);
    }

    // Convert word mappings to DTO list
    private List<ParsedWordMappingDTO> convertToDTO(Map<String, List<Integer>> wordMappings, Long articleId) {
        List<ParsedWordMappingDTO> ans = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : wordMappings.entrySet()) {
            try {
                String offsetsAsJson = objectMapper.writeValueAsString(entry.getValue());
                ParsedWordMappingDTO wordMappingDTO = new ParsedWordMappingDTO(entry.getKey(), articleId, offsetsAsJson);
                ans.add(wordMappingDTO);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return ans;
    }

    // Send the parsed word mappings to the DAL service
    protected void sendParsedDataToDal(List<ParsedWordMappingDTO> wordMappings) {
        String dalServiceUrl = this.articleServiceUrl  +"/api/word-mappings/save-all";
        // Adjust the DAL service URL as necessary
        restTemplate.postForEntity(dalServiceUrl, wordMappings, Void.class);
    }

    // Normalize content by removing accents and converting to lowercase
    protected String convertToLower(String word) {
        return word.toLowerCase(Locale.ROOT);
    }
}
