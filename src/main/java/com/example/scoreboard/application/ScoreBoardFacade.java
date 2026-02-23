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

    public void startGame(String homeName, String awayName) {
        scoreBoard.startGame(new Team(homeName), new Team(awayName));
    }

    public void updateScore(String homeName,
                            String awayName,
                            int homeScore,
                            int awayScore) {

        scoreBoard.updateScore(
                new Team(homeName),
                new Team(awayName),
                homeScore,
                awayScore
        );
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
    ) {}
}