package com.example.apigetawayservice;

import com.example.apigetawayservice.controller.MenuController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.CommandLineRunner;


@SpringBootApplication
public class ApiGetawayServiceApplication implements CommandLineRunner {
    private final ApplicationContext context;

    @Autowired
    public ApiGetawayServiceApplication(ApplicationContext context) {
        this.context = context;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiGetawayServiceApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        // Fetch the MenuController from the context instead of injecting it
        MenuController menuController = context.getBean(MenuController.class);
        menuController.displayMenu();
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
