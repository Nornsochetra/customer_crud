package com.kosign.customer_crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootApplication
public class CustomerCrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerCrudApplication.class, args);
    }
}
