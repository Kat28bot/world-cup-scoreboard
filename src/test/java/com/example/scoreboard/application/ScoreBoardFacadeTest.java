package com.example.scoreboard.application;

import com.example.scoreboard.domain.ScoreBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class ScoreBoardFacadeTest {


    private ScoreBoard scoreBoard;
    private ScoreBoardFacade facade;

    @BeforeEach
    void setUp() {
         //Use a real ScoreBoard
        scoreBoard = new ScoreBoard();
        facade = new ScoreBoardFacade(scoreBoard);
    }

    @Test
    void shouldStartGameSuccessfully() {
        ScoreBoardFacade.StartGameResult result =
                facade.startGame("Poland", "Germany");

        assertThat(result.success()).isTrue();
        assertThat(result.message()).isNull();
    }

    @Test
    void shouldRejectSameTeams() {
        ScoreBoardFacade.StartGameResult result =
                facade.startGame("Poland", "Poland");

        assertThat(result.success()).isFalse();
        assertThat(result.message())
                .isEqualTo("Home and Away teams cannot be the same");
    }

    @Test
    void shouldRejectIfHomeTeamAlreadyPlaying() {
        facade.startGame("Poland", "Spain");

        ScoreBoardFacade.StartGameResult result =
                facade.startGame("Poland", "Germany");

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("Poland is already playing");
    }

    @Test
    void shouldRejectIfAwayTeamAlreadyPlaying() {
        facade.startGame("Spain", "Germany");

        ScoreBoardFacade.StartGameResult result =
                facade.startGame("Poland", "Germany");

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("Germany is already playing");
    }

    @Test
    void shouldUpdateAndFinishGame() {
        facade.startGame("Poland", "Germany");

        facade.updateScore("Poland", "Germany", 2, 1);
        var summary = facade.getSummary();
        assertThat(summary).hasSize(1);
        assertThat(summary.get(0).homeScore()).isEqualTo(2);
        assertThat(summary.get(0).awayScore()).isEqualTo(1);

        facade.finishGame("Poland", "Germany");
        assertThat(facade.getSummary()).isEmpty();
    }
}