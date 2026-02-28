package com.busgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BusCompanyGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusCompanyGameApplication.class, args);
    }

}
