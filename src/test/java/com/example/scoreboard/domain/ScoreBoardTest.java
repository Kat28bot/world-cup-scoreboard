package com.example.scoreboard.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.ArrayList;
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
        Game game = scoreBoard.startGame(brazil, argentina);
        assertEquals(0, game.getScore().getHome());
        assertEquals(0, game.getScore().getAway());
    }

    @Test
    void shouldNotStartGameIfTeamAlreadyPlaying() {
        scoreBoard.startGame(brazil, argentina);

        assertThrows(IllegalStateException.class, () -> scoreBoard.startGame(brazil, germany));
        assertThrows(IllegalStateException.class, () -> scoreBoard.startGame(germany, argentina));
    }

    @Test
    void shouldNotStartGameWithSameTeam() {
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame(brazil, brazil));
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
    void shouldNotUpdateScoreForFinishedGame() {
        scoreBoard.startGame(brazil, argentina);
        scoreBoard.updateScore(brazil, argentina, 2, 2);
        scoreBoard.finishGame(brazil, argentina);

        assertThrows(IllegalStateException.class, () ->
                scoreBoard.updateScore(brazil, argentina, 3, 3));

        assertTrue(scoreBoard.getSummary().isEmpty()); // still finished/removed
    }
    @Test
    void tieBreakShouldBeMostRecentlyStartedNotMostRecentlyUpdated() {
        ScoreBoard board = new ScoreBoard();
        Team a = new Team("A");
        Team b = new Team("B");
        Team c = new Team("C");
        Team d = new Team("D");

        Game older = board.startGame(a, b); // startOrder = 1
        Game newer = board.startGame(c, d); // startOrder = 2

        // Make totals equal, but update the older game *after* the newer one.
        board.updateScore(c, d, 2, 3); // total 5 (newer updated first)
        board.updateScore(a, b, 4, 1); // total 5 (older updated later)

        List<Game> summary = board.getSummary();
        assertEquals(2, summary.size());

        // Tie-break must be by start order (most recently started), not last updated.
        assertEquals(newer, summary.get(0));
        assertEquals(older, summary.get(1));
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentGame() {
        Team poland = new Team("Poland");

        assertThrows(IllegalStateException.class, () ->
                scoreBoard.updateScore(poland, germany, 1, 1));
    }

    @Test
    void shouldThrowWhenFinishingNonExistentGame() {
        Team poland = new Team("Poland");

        assertThrows(IllegalStateException.class, () ->
                scoreBoard.finishGame(poland, germany));
    }

    @Test
    void shouldNotAllowStartingSameMatchupTwice() {
        Team mexico = new Team("Mexico");
        Team canada = new Team("Canada");

        scoreBoard.startGame(mexico, canada);

        assertThrows(IllegalStateException.class, () ->
                scoreBoard.startGame(mexico, canada));
    }
    @Test
    void shouldReturnSummaryOrderedByTotalScoreAndMostRecent() {
        Game game1 = scoreBoard.startGame(brazil, argentina);
        scoreBoard.updateScore(brazil, argentina, 2, 2); // total 4

        Team france = new Team("France");
        Game game2 = scoreBoard.startGame(germany, france);
        scoreBoard.updateScore(germany, france, 3, 2); // total 5

        Team spain = new Team("Spain");
        Team italy = new Team("Italy");
        Game game3 = scoreBoard.startGame(spain, italy);
        scoreBoard.updateScore(spain, italy, 1, 1); // total 2

        List<Game> summary = scoreBoard.getSummary();

        assertEquals(3, summary.size());
        assertEquals(game2, summary.get(0)); // total 5
        assertEquals(game1, summary.get(1)); // total 4
        assertEquals(game3, summary.get(2)); // total 2
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
        List<Game> results = Collections.synchronizedList(new ArrayList<>());

        // Teams paired: (A,B), (C,D), (E,F)
        for (int i = 0; i < teams.size(); i += 2) {
            int idx = i;
            threads.add(new Thread(() -> results.add(board.startGame(teams.get(idx), teams.get(idx + 1)))));
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        assertEquals(3, results.size());

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

        Game g1 = board.startGame(a, b); // order = 1
        Game g2 = board.startGame(c, d); // order = 2

        board.updateScore(a, b, 3, 2); // total 5
        board.updateScore(c, d, 4, 1); // total 5

        List<Game> summary = board.getSummary();
        assertEquals(2, summary.size());

        assertEquals(g2, summary.get(0));
        assertEquals(g1, summary.get(1));
    }

}