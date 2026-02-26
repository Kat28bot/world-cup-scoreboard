package com.example.scoreboard.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ScoreBoard {
    private static final Logger log = LoggerFactory.getLogger(ScoreBoard.class);

    private final ConcurrentHashMap<GameKey, Game> games = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    // ------------------------
    // ScoreBoard methods:
    // ------------------------

    public synchronized Game startGame(Team home, Team away) {
        validateTeams(home, away);

        long order = sequence.incrementAndGet();
        Game game = new Game(home, away, order);
        GameKey key = new GameKey(home, away);
        games.put(key, game);

        log.info("Game started: {} vs {}", home.getName(), away.getName());
        return game;
    }

    private void validateTeams(Team home, Team away) {
        if (home.equals(away)) {
            throw new IllegalArgumentException("Home and Away teams cannot be the same");
        }

        if (isTeamCurrentlyPlaying(home)) {
            throw new IllegalStateException(home.getName() + " is already playing another game");
        }

        if (isTeamCurrentlyPlaying(away)) {
            throw new IllegalStateException(away.getName() + " is already playing another game");
        }
    }

    public void updateScore(Team home, Team away, int homeScore, int awayScore) {
        Game game = getRequiredExistingGame(home, away);
        game.updateScore(homeScore, awayScore);

        log.debug("Score updated: {} vs {} -> {}:{}",
                home.getName(), away.getName(), homeScore, awayScore);

    }

    public void finishGame(Team home, Team away) {

        GameKey key = new GameKey(home, away);
        Game removed = games.remove(key);

        if (removed == null) {
            throw new IllegalStateException("Game " + home.getName() + " vs " + away.getName() + " doesn't exist");
        }

        log.info("Game finished: {} vs {}", home.getName(), away.getName());
    }

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
    // Helpers
    // ------------------------

    private Game getRequiredExistingGame(Team home, Team away) {
        Game game = games.get(new GameKey(home, away));
        if (game == null) {
            throw new IllegalStateException("Game " + home.getName() + " vs " + away.getName() + " doesn't exist");
        }
        return game;
    }

    public boolean isTeamCurrentlyPlaying(Team team) {

        return games.values().stream()
                .anyMatch(g ->
                        g.getHomeTeam().equals(team) ||
                                g.getAwayTeam().equals(team));
    }

    // ------------------------
    // Key
    // ------------------------

    private record GameKey(Team home, Team away) {
    }
}