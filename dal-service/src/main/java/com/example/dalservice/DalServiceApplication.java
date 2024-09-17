package com.example.dalservice;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "DAL Service API", version = "1.0", description = "Documentation for DAL Service"))

public class DalServiceApplication {

    public static void main(String[] args) {
        System.out.println();
        SpringApplication.run(DalServiceApplication.class, args);
    }

}
