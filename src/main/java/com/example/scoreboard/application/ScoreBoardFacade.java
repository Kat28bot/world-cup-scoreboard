package com.example.scoreboard.application;

import com.example.scoreboard.domain.Game;
import com.example.scoreboard.domain.ScoreBoard;
import com.example.scoreboard.domain.Team;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ScoreBoardFacade {

    private final ScoreBoard scoreBoard;

    public ScoreBoardFacade(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public StartGameResult startGame(String homeName, String awayName) {
        Team home = new Team(homeName);
        Team away = new Team(awayName);

        Optional<Game> maybeGame = scoreBoard.startGame(home, away);

        if (maybeGame.isPresent()) {
            return new StartGameResult(true, null);
        }

        if (home.equals(away)) {
            return new StartGameResult(false, "Home and Away teams cannot be the same");
        }
        if (scoreBoard.isTeamPlaying(home)) {
            return new StartGameResult(false, homeName + " is already playing another game");
        }
        if (scoreBoard.isTeamPlaying(away)) {
            return new StartGameResult(false, awayName + " is already playing another game");
        }

        return new StartGameResult(false, "Cannot start game");
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


    public record StartGameResult(boolean success, String message) {
    }

    public record GameView(
            String homeTeam,
            String awayTeam,
            int homeScore,
            int awayScore
    ) {
    }
}