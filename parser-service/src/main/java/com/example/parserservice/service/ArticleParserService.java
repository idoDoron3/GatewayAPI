package com.example.parserservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.parserservice.DTO.ParsedWordMappingDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ArticleParserService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newCachedThreadPool();  // Dynamic thread pool

    // Minimum chunk size to justify using multiple threads
    private static final int MIN_CHUNK_SIZE = 500;

    public ArticleParserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Main function - parse the article to map each word's offset using concurrency if applicable
    public List<ParsedWordMappingDTO> parseArticle(String content, Long articleId) throws InterruptedException, ExecutionException {
        // Normalize the content to remove accents and make it case-insensitive
        String normalizedContent = normalizeContent(content);
        int contentLength = normalizedContent.length();

        // If the content is small, process in a single thread
        if (contentLength < MIN_CHUNK_SIZE) {
            return parseSingleThread(normalizedContent, articleId);
        }

        // Determine the number of threads based on the content size and available processors
        int numThreads = Math.min(Runtime.getRuntime().availableProcessors(), contentLength / MIN_CHUNK_SIZE);
        int chunkSize = contentLength / numThreads;

        // List to hold future tasks for each chunk
        List<Future<Map<String, List<Integer>>>> futures = new ArrayList<>();

        // Submit parsing tasks for each chunk
        for (int i = 0; i < numThreads; i++) {
            final int start = (i == 0) ? 0 : adjustChunkStart(normalizedContent, adjustChunkEnd(normalizedContent, i * chunkSize) + 1);
            final int end = (i == numThreads - 1) ? contentLength : adjustChunkEnd(normalizedContent, (i + 1) * chunkSize);

            String chunk = normalizedContent.substring(start, end);
            futures.add(executorService.submit(() -> parseChunk(chunk, start)));
        }

        // Merge the results from all threads
        Map<String, List<Integer>> wordMappings = new HashMap<>();
        for (Future<Map<String, List<Integer>>> future : futures) {
            Map<String, List<Integer>> result = future.get();
            result.forEach((word, offsets) -> {
                wordMappings.computeIfAbsent(word, k -> new ArrayList<>()).addAll(offsets);
            });
        }

        // Convert map to list of ParsedWordMappingDTO objects
        List<ParsedWordMappingDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : wordMappings.entrySet()) {
            try {
                String offsetsAsJson = objectMapper.writeValueAsString(entry.getValue());
                ParsedWordMappingDTO wordMappingDTO = new ParsedWordMappingDTO(entry.getKey(), articleId, offsetsAsJson);
                result.add(wordMappingDTO);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Send parsed word mappings to the DAL service
        sendParsedDataToDal(result);
        return result;
    }

    // Adjust chunk end to ensure no word is split
// Adjust chunk end to ensure no word is split
    private int adjustChunkEnd(String content, int chunkEnd) {
        // Edge case: If we're at the end of the content, return the full length
        if (chunkEnd >= content.length()) {
            return content.length();
        }

        // Move backward to the nearest space to avoid splitting a word
        while (chunkEnd > 0 && content.charAt(chunkEnd) != ' ') {
            chunkEnd--;
        }

        // If we couldn't find a space, we return the chunkEnd as is (for very short chunks without spaces)
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
        List<ParsedWordMappingDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : wordMappings.entrySet()) {
            try {
                String offsetsAsJson = objectMapper.writeValueAsString(entry.getValue());
                ParsedWordMappingDTO wordMappingDTO = new ParsedWordMappingDTO(entry.getKey(), articleId, offsetsAsJson);
                result.add(wordMappingDTO);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Send parsed word mappings to the DAL service
        sendParsedDataToDal(result);
        return result;
    }

    // Parse a chunk of the article content and return word mappings
    private Map<String, List<Integer>> parseChunk(String content, int offsetBase) {
        Map<String, List<Integer>> wordMappings = new HashMap<>();
        Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = wordPattern.matcher(content);

        // Iterate over each match and record the word and its offset
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();  // Case-insensitive matching
            int offset = matcher.start() + offsetBase;  // Adjust offset by the base of the chunk

            // Add the offset to the word's list of offsets
            wordMappings.computeIfAbsent(word, k -> new ArrayList<>()).add(offset);
        }
        return wordMappings;
    }

    // Send the parsed word mappings to the DAL service
    protected void sendParsedDataToDal(List<ParsedWordMappingDTO> wordMappings) {
        String dalServiceUrl = "http://localhost:8081/api/word-mappings/save-all";//TODO
        // Adjust the DAL service URL as necessary
        restTemplate.postForEntity(dalServiceUrl, wordMappings, Void.class);
    }

    // Normalize content by removing accents and converting to lowercase
    protected String normalizeContent(String content) {
        // Normalize accented characters to their base forms
        String normalized = Normalizer.normalize(content, Normalizer.Form.NFD);
        // Remove all accents by using a regex to strip out non-ASCII characters
        normalized = normalized.replaceAll("\\p{M}", "");
        // Convert to lowercase for case-insensitive comparison
        return normalized.toLowerCase();
    }
}
