package com.example.scoreboard;

import com.example.scoreboard.domain.Game;
import com.example.scoreboard.domain.ScoreBoard;
import com.example.scoreboard.domain.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

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
        Optional<Game> maybeGame = scoreBoard.startGame(brazil, argentina);
        assertTrue(maybeGame.isPresent());
        Game game = maybeGame.get();
        assertEquals(0, game.getScore().getHome());
        assertEquals(0, game.getScore().getAway());
    }

    @Test
    void shouldNotStartGameIfTeamAlreadyPlaying() {
        scoreBoard.startGame(brazil, argentina);

        // Attempt to start game with a team already playing
        Optional<Game> game1 = scoreBoard.startGame(brazil, germany);
        assertTrue(game1.isEmpty());

        Optional<Game> game2 = scoreBoard.startGame(germany, argentina);
        assertTrue(game2.isEmpty());
    }

    @Test
    void shouldNotStartGameWithSameTeam() {
        Optional<Game> game = scoreBoard.startGame(brazil, brazil);
        assertTrue(game.isEmpty());
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
        Optional<Game> maybeGame1 = scoreBoard.startGame(brazil, argentina);
        Game game1 = maybeGame1.get();
        scoreBoard.updateScore(brazil, argentina, 2, 2); // total 4

        Team france = new Team("France");
        Optional<Game> maybeGame2 = scoreBoard.startGame(germany, france);
        Game game2 = maybeGame2.get();
        scoreBoard.updateScore(germany, france, 3, 2); // total 5

        Optional<Game> maybeGame3 = scoreBoard.startGame(new Team("Spain"), new Team("Italy"));
        Game game3 = maybeGame3.get();
        scoreBoard.updateScore(new Team("Spain"), new Team("Italy"), 1, 1); // total 2

        List<Game> summary = scoreBoard.getSummary();

        assertEquals(3, summary.size());
        assertEquals(game2, summary.get(0)); // total 5
        assertEquals(game1, summary.get(1)); // total 4
        assertEquals(game3, summary.get(2)); // total 2
    }
}