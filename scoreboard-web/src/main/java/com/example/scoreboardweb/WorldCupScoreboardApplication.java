package com.example.scoreboardweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.scoreboardweb")
public class WorldCupScoreboardApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorldCupScoreboardApplication.class, args);

    }
}
