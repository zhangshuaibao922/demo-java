package com.cardo.demojava;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class DemoJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoJavaApplication.class, args);
    }

}
