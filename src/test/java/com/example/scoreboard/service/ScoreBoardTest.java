package com.example.scoreboard.service;

import com.example.scoreboard.domain.Game;
import com.example.scoreboard.domain.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreBoardTest {

    private ScoreBoard scoreBoard;
    private Team brazil;
    private Team argentina;
    private Team germany;

    @BeforeEach
    void setup() {
        scoreBoard = new ScoreBoard();
        brazil = new Team("Brazil");
        argentina = new Team("Argentina");
        germany = new Team("Germany");
    }

    @Test
    void shouldStartGame() {
        Game game = scoreBoard.startGame(brazil, argentina);
        assertNotNull(game);
        assertEquals(0, game.getScore().getHome());
        assertEquals(0, game.getScore().getAway());
    }

    @Test
    void shouldNotStartGameIfTeamAlreadyPlaying() {
        scoreBoard.startGame(brazil, argentina);
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame(brazil, germany));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame(germany, argentina));
    }

    @Test
    void shouldFinishGame() {
        Game game = scoreBoard.startGame(brazil, argentina);
        scoreBoard.finishGame(game);
        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void shouldUpdateScore() {
        Game game = scoreBoard.startGame(brazil, argentina);
        scoreBoard.updateScore(game, 2, 3);
        assertEquals(2, game.getScore().getHome());
        assertEquals(3, game.getScore().getAway());
    }

    @Test
    void shouldThrowWhenUpdatingScoreWithNegatives() {
        Game game = scoreBoard.startGame(brazil, argentina);
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.updateScore(game, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.updateScore(game, 0, -2));
    }

    @Test
    void shouldReturnSummaryOrderedByTotalScoreAndMostRecent() {
        Game game1 = scoreBoard.startGame(brazil, argentina);
        scoreBoard.updateScore(game1, 2, 2); // total 4

        Game game2 = scoreBoard.startGame(germany, new Team("France"));
        scoreBoard.updateScore(game2, 3, 2); // total 5

        Game game3 = scoreBoard.startGame(new Team("Spain"), new Team("Italy"));
        scoreBoard.updateScore(game3, 1, 1); // total 2

        List<Game> summary = scoreBoard.getSummary();

        assertEquals(3, summary.size());
        assertEquals(game2, summary.get(0)); // total 5
        assertEquals(game1, summary.get(1)); // total 4
        assertEquals(game3, summary.get(2)); // total 2
    }
}
