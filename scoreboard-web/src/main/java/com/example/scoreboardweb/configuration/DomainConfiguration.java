package com.example.scoreboardweb.configuration;

import com.example.scoreboard.domain.ScoreBoard;
import com.example.scoreboardweb.application.ScoreBoardFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {
    @Bean
    public ScoreBoard scoreBoard() {
        return new ScoreBoard();
    }

    @Bean
    public ScoreBoardFacade scoreBoardFacade(ScoreBoard scoreBoard) {
        return new ScoreBoardFacade(scoreBoard);
    }
}
