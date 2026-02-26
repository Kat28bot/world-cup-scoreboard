package com.example.scoreboard.application;

import com.example.scoreboard.domain.ScoreBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScoreBoardFacadeTest {


    private ScoreBoard scoreBoard;
    private ScoreBoardFacade facade;

    @BeforeEach
    void setUp() {
        scoreBoard = new ScoreBoard();
        facade = new ScoreBoardFacade(scoreBoard);
    }

    @Test
    void shouldStartGameSuccessfully() {
        facade.startGame("Poland", "Germany");
        assertThat(facade.getSummary()).hasSize(1);
    }

    @Test
    void shouldRejectSameTeams() {
        assertThatThrownBy(() -> facade.startGame("Poland", "Poland"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Home and Away teams cannot be the same");
    }

    @Test
    void shouldRejectIfHomeTeamAlreadyPlaying() {
        facade.startGame("Poland", "Spain");

        assertThatThrownBy(() -> facade.startGame("Poland", "Germany"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Poland");
    }

    @Test
    void shouldRejectIfAwayTeamAlreadyPlaying() {
        facade.startGame("Spain", "Germany");

        assertThatThrownBy(() -> facade.startGame("Poland", "Germany"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Germany");
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