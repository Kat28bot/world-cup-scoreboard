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
            return new StartGameResult(false, home.getName() + " is already playing another game");
        }
        if (scoreBoard.isTeamPlaying(away)) {
            return new StartGameResult(false, away.getName() + " is already playing another game");
        }

        return new StartGameResult(false, "Cannot start game");
    }

    public UpdateScoreResult updateScore(String homeName,
                            String awayName,
                            int homeScore,
                            int awayScore) {

        Team home = new Team(homeName);
        Team away = new Team(awayName);

        Game game = scoreBoard.getExistingGame(home, away);
        if (game == null) {
            return new UpdateScoreResult(false, "Game " + homeName + " vs " + awayName + " doesn't exist");
        }

        scoreBoard.updateScore(home, away, homeScore, awayScore);
        return new UpdateScoreResult(true, null);
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


    public record StartGameResult(boolean success, String message) {}
    public record UpdateScoreResult(boolean success, String message) {}

    public record GameView(
            String homeTeam,
            String awayTeam,
            int homeScore,
            int awayScore
    ) {
    }
}