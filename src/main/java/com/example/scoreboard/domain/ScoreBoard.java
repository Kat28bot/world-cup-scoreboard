package com.example.scoreboard.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ScoreBoard {
    private static final Logger log = LoggerFactory.getLogger(ScoreBoard.class);

    private final ConcurrentHashMap<GameKey, Game> games = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    // ------------------------
    // Start Game
    // ------------------------

    public synchronized Optional<Game> startGame(Team home, Team away) {

        if (home.equals(away)) {
            log.warn("Cannot start game: {} vs {} - teams cannot be the same",
                    home.getName(), away.getName());
            return Optional.empty();
        }

        if (isTeamPlaying(home) || isTeamPlaying(away)) {
            log.warn("Cannot start game: {} vs {} - One of the teams is already playing",
                    home.getName(), away.getName());
            return Optional.empty();
        }

        long order = sequence.incrementAndGet();
        Game game = new Game(home, away, order);
        GameKey key = new GameKey(home, away);
        games.put(key, game);

        log.info("Game started: {} vs {}", home.getName(), away.getName());
        return Optional.of(game);
    }

    // ------------------------
    // Update Score (atomic)
    // ------------------------

    public void updateScore(Team home, Team away, int homeScore, int awayScore) {

        Game game = getExistingGame(home, away);
        if (game != null) {
            game.updateScore(homeScore, awayScore);
            log.debug("Score updated: {} vs {} -> {}:{}",
                    home.getName(), away.getName(), homeScore, awayScore);
        }
    }

    // ------------------------
    // Finish Game
    // ------------------------

    public void finishGame(Team home, Team away) {

        GameKey key = new GameKey(home, away);
        Game removed = games.remove(key);

        if (removed == null) {
           log.warn("Game not found: {} vs {}", home.getName(), away.getName());
        }
        else{
            log.info("Game finished: {} vs {}", home.getName(), away.getName());
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
            log.warn("Attempted to update score for a non-existent game: {} vs {}", home.getName(), away.getName());
        }

        return game;
    }

    public boolean isTeamPlaying(Team team) {

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