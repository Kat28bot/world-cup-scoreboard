package com.example.scoreboard.domain;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ScoreBoard {

    private final ConcurrentHashMap<GameKey, Game> games = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    // ------------------------
    // Start Game
    // ------------------------

    public Game startGame(Team home, Team away) {

        if (home.equals(away)) {
            throw new IllegalArgumentException("Teams cannot be the same");
        }

        // Ensure teams are not already playing
        if (isTeamPlaying(home) || isTeamPlaying(away)) {
            throw new IllegalArgumentException("One of the teams is already playing");
        }

        long order = sequence.incrementAndGet();
        Game game = new Game(home, away, order);

        GameKey key = new GameKey(home, away);
        games.put(key, game);

        return game;
    }

    // ------------------------
    // Update Score (atomic)
    // ------------------------

    public void updateScore(Team home, Team away, int homeScore, int awayScore) {

        Game game = getExistingGame(home, away);
        game.updateScore(homeScore, awayScore);
    }

    // ------------------------
    // Finish Game
    // ------------------------

    public void finishGame(Team home, Team away) {

        GameKey key = new GameKey(home, away);
        Game removed = games.remove(key);

        if (removed == null) {
            throw new IllegalArgumentException("Game not found");
        }
    }

    // ------------------------
    // Summary
    // ------------------------

    public List<Game> getSummary() {

        return games.values().stream()
                .sorted(
                        Comparator
                                .comparingInt(Game::totalScore)
                                .thenComparing(Game::getStartOrder)
                                .reversed()
                )
                .toList();
    }

    // ------------------------
    // Internal Helpers
    // ------------------------

    private Game getExistingGame(Team home, Team away) {

        Game game = games.get(new GameKey(home, away));

        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        return game;
    }

    private boolean isTeamPlaying(Team team) {

        return games.values().stream()
                .anyMatch(g ->
                        g.getHomeTeam().equals(team) ||
                                g.getAwayTeam().equals(team));
    }

    // ------------------------
    // Key
    // ------------------------

    private record GameKey(Team home, Team away) {}
}