package com.example.scoreboard.configuration;

import com.example.scoreboard.domain.ScoreBoard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {
    @Bean
    public ScoreBoard scoreBoard() {
        return new ScoreBoard();
    }
}
