package com.example.scoreboard;

import com.example.scoreboard.domain.Game;
import com.example.scoreboard.domain.ScoreBoard;
import com.example.scoreboard.domain.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

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

    @Test
    void shouldNotUpdateScoreForFinishedGame() {
        scoreBoard.startGame(brazil, argentina);
        scoreBoard.updateScore(brazil, argentina, 2, 2);
        scoreBoard.finishGame(brazil, argentina);

        scoreBoard.updateScore(brazil, argentina, 3, 3);
        assertTrue(scoreBoard.getSummary().isEmpty()); // Should stay finished
    }

    @Test
    void shouldHandleConcurrentGameStarts() throws InterruptedException {
        // Multi-threaded test with ConcurrentHashMap
        ScoreBoard board = new ScoreBoard();
        List<Team> teams = List.of(
                new Team("A"), new Team("B"), new Team("C"), new Team("D"),
                new Team("E"), new Team("F")
        );

        List<Thread> threads = new ArrayList<>();
        List<Optional<Game>> results = Collections.synchronizedList(new ArrayList<>());

        // Teams paired: (A,B), (C,D), (E,F)
        for (int i = 0; i < teams.size(); i += 2) {
            int idx = i;
            threads.add(new Thread(() -> {
                results.add(board.startGame(teams.get(idx), teams.get(idx+1)));
            }));
        }

        // Start all threads nearly simultaneously
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        // Verify all games started
        assertEquals(3, results.size());
        results.forEach(opt -> assertTrue(opt.isPresent()));

        // Verify ScoreBoard contains all matches, none with same teams
        List<Game> allGames = board.getSummary();
        assertEquals(3, allGames.size());
        Set<Set<String>> pairs = new HashSet<>();
        for (Game g : allGames) {
            Set<String> pair = Set.of(g.getHomeTeam().getName(), g.getAwayTeam().getName());
            assertTrue(pairs.add(pair)); // add returns false if duplicate
        }
    }

    @Test
    void shouldPreserveSummaryOrderWhenScoresSame() {
        ScoreBoard board = new ScoreBoard();
        Team a = new Team("A");
        Team b = new Team("B");
        Team c = new Team("C");
        Team d = new Team("D");

        Optional<Game> g1 = board.startGame(a, b); // order = 1
        Optional<Game> g2 = board.startGame(c, d); // order = 2

        assertTrue(g1.isPresent());
        assertTrue(g2.isPresent());

        board.updateScore(a, b, 3, 2); // total 5
        board.updateScore(c, d, 4, 1); // total 5

        List<Game> summary = board.getSummary();
        assertEquals(2, summary.size());

        assertEquals(g2.get(), summary.get(0));
        assertEquals(g1.get(), summary.get(1));
    }
}