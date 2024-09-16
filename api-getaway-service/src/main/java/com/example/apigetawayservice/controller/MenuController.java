package com.example.apigetawayservice.controller;

import com.example.apigetawayservice.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class MenuController {


    private final MenuService menuService;
    // Constructor injection for MenuService
    @Autowired

    public MenuController(@Lazy MenuService menuService) {
        this.menuService = menuService;
    }

    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Create an article");
            System.out.println("2. Get an article by ID");
            System.out.println("3. Find words in an article");
            System.out.println("4. Delete an article by ID");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    menuService.createArticle(scanner);
                    break;
                case 2:
                    menuService.getArticleById(scanner);
                    break;
                case 3:
                    menuService.findWordsInArticle(scanner);
                    break;
                case 4:
                    menuService.deleteArticleById(scanner);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
