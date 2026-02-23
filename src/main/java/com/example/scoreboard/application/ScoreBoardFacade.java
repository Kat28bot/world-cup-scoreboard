package com.example.scoreboard.application;

import com.example.scoreboard.domain.Game;
import com.example.scoreboard.domain.ScoreBoard;
import com.example.scoreboard.domain.Team;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ScoreBoardFacade {

    private final ScoreBoard scoreBoard;

    public ScoreBoardFacade(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    // -------------------------
    // Use Cases
    // -------------------------

    public void startGame(String homeName, String awayName) {
        Team home = new Team(homeName);
        Team away = new Team(awayName);
        scoreBoard.startGame(home, away);
    }

    public void updateScore(String homeName,
                            String awayName,
                            int homeScore,
                            int awayScore) {

        Game game = getExistingGame(homeName, awayName);
        scoreBoard.updateScore(game, homeScore, awayScore);
    }

    public void finishGame(String homeName, String awayName) {
        Game game = getExistingGame(homeName, awayName);
        scoreBoard.finishGame(game);
    }

    public List<GameView> getSummary() {
        return scoreBoard.getSummary()
                .stream()
                .map(this::toView)
                .toList();
    }

    // -------------------------
    // Internal helpers
    // -------------------------

    private Game getExistingGame(String homeName, String awayName) {

        Team home = new Team(homeName);
        Team away = new Team(awayName);

        return scoreBoard.findGame(home, away)
                .orElseThrow(() ->
                        new IllegalArgumentException("Game not found"));
    }

    private GameView toView(Game game) {
        return new GameView(
                game.getHomeTeam().getName(),
                game.getAwayTeam().getName(),
                game.getScore().getHome(),
                game.getScore().getAway()
        );
    }

    // -------------------------
    // View Projection
    // -------------------------

    public record GameView(
            String homeTeam,
            String awayTeam,
            int homeScore,
            int awayScore
    ) {}
}