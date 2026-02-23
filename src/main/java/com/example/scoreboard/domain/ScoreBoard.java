package com.example.scoreboard.domain;

import java.util.*;

public class ScoreBoard {

    private final List<Game> games = new ArrayList<>();
    private long sequence = 0;

    public synchronized  Game startGame(Team home, Team away) {
        Objects.requireNonNull(home, "Home team cannot be null");
        Objects.requireNonNull(away, "Away team cannot be null");

        // Check if teams are already playing
        for (Game g : games) {
            if (g.getHomeTeam().equals(home) || g.getAwayTeam().equals(home)
                    || g.getHomeTeam().equals(away) || g.getAwayTeam().equals(away)) {
                throw new IllegalArgumentException("One of the teams is already playing");
            }
        }

        Game game = new Game(home, away,++sequence);
        games.add(game);
        return game;
    }

    public synchronized  void finishGame(Game game) {
        if (!games.remove(game)) {
            throw new IllegalArgumentException("Game not found");
        }
    }

    public synchronized  void updateScore(Game game, int home, int away) {
        if (!games.contains(game)) {
            throw new IllegalArgumentException("Game not found");
        }
        game.updateScore(home, away);
    }

    public synchronized  List<Game> getSummary() {
        return games.stream()
                .sorted(
                        Comparator
                                .comparingInt(Game::totalScore)
                                .thenComparing(Game::getStartOrder)
                                .reversed()

                )
                .toList();
    }
    public synchronized  Optional<Game> findGame(Team home, Team away) {
        return games.stream()
                .filter(g ->
                        g.getHomeTeam().equals(home) &&
                                g.getAwayTeam().equals(away))
                .findFirst();
    }

}
