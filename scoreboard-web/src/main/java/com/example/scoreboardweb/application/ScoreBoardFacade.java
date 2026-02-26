package com.example.scoreboardweb.application;

import com.example.scoreboard.domain.Game;
import com.example.scoreboard.domain.ScoreBoard;
import com.example.scoreboard.domain.Team;

import java.util.List;


public class ScoreBoardFacade {

    private final ScoreBoard scoreBoard;

    public ScoreBoardFacade(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public Game startGame(String homeName, String awayName) {
        Team home = new Team(homeName);
        Team away = new Team(awayName);
        return scoreBoard.startGame(home, away);
    }

    public void updateScore(String homeName,
                            String awayName,
                            int homeScore,
                            int awayScore) {

        Team home = new Team(homeName);
        Team away = new Team(awayName);
        scoreBoard.updateScore(home, away, homeScore, awayScore);
    }

    public void finishGame(String homeName, String awayName) {
        scoreBoard.finishGame(
                new Team(homeName),
                new Team(awayName)
        );
    }

    public List<GameView> getSummary() {
        return scoreBoard.getSummary()
                .stream()
                .map(this::toView)
                .toList();
    }

    private GameView toView(Game game) {
        return new GameView(
                game.getHomeTeam().getName(),
                game.getAwayTeam().getName(),
                game.getScore().getHome(),
                game.getScore().getAway()
        );
    }

    public record GameView(
            String homeTeam,
            String awayTeam,
            int homeScore,
            int awayScore
    ) {
    }
}