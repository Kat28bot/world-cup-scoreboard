package com.example.scoreboard;

import com.example.scoreboard.domain.Game;
import com.example.scoreboard.domain.ScoreBoard;
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

        assertThrows(IllegalArgumentException.class, () ->
                scoreBoard.startGame(brazil, germany));

        assertThrows(IllegalArgumentException.class, () ->
                scoreBoard.startGame(germany, argentina));
    }

    @Test
    void shouldFinishGame() {
        scoreBoard.startGame(brazil, argentina);
        scoreBoard.finishGame(brazil, argentina);
        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void shouldUpdateScore() {
        scoreBoard.startGame(brazil, argentina);
        scoreBoard.updateScore(brazil, argentina, 2, 3);

        Game game = scoreBoard.startGame(new Team("Dummy1"), new Team("Dummy2")); // dummy to fetch summary
        // Fetch the game from summary
        Game fetched = scoreBoard.getSummary().stream()
                .filter(g -> g.getHomeTeam().equals(brazil) && g.getAwayTeam().equals(argentina))
                .findFirst().orElseThrow();
        assertEquals(2, fetched.getScore().getHome());
        assertEquals(3, fetched.getScore().getAway());
    }

    @Test
    void shouldThrowWhenUpdatingScoreWithNegatives() {
        scoreBoard.startGame(brazil, argentina);

        assertThrows(IllegalArgumentException.class, () ->
                scoreBoard.updateScore(brazil, argentina, -1, 0));

        assertThrows(IllegalArgumentException.class, () ->
                scoreBoard.updateScore(brazil, argentina, 0, -2));
    }

    @Test
    void shouldReturnSummaryOrderedByTotalScoreAndMostRecent() {
        Game game1 = scoreBoard.startGame(brazil, argentina);
        scoreBoard.updateScore(brazil, argentina, 2, 2); // total 4

        Team france = new Team("France");
        Game game2 = scoreBoard.startGame(germany, france);
        scoreBoard.updateScore(germany, france, 3, 2); // total 5

        Game game3 = scoreBoard.startGame(new Team("Spain"), new Team("Italy"));
        scoreBoard.updateScore(new Team("Spain"), new Team("Italy"), 1, 1); // total 2

        List<Game> summary = scoreBoard.getSummary();

        assertEquals(3, summary.size());
        assertEquals(game2, summary.get(0)); // total 5
        assertEquals(game1, summary.get(1)); // total 4
        assertEquals(game3, summary.get(2)); // total 2
    }
}