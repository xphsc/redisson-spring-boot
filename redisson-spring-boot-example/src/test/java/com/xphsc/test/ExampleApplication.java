package com.xphsc.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.function.Supplier;

@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) {

        ConfigurableApplicationContext configurableApplicationContext= SpringApplication.run(ExampleApplication.class, args);
    }
}
