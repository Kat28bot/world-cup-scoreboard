package com.example.scoreboard.demo;

import com.example.scoreboard.domain.Game;
import com.example.scoreboard.domain.ScoreBoard;
import com.example.scoreboard.domain.Team;

public final class WorldCupScoreBoardLibraryDemo {

    public static void main(String[] args) {
        ScoreBoard scoreBoard = new ScoreBoard();

        // Start games (initial score 0-0)
        scoreBoard.startGame(new Team("Mexico"), new Team("Canada"));

        // Demonstrate an invalid operation (expected failure): duplicate / team already playing
        try {
            scoreBoard.startGame(new Team("Mexico"), new Team("Canada"));
        } catch (RuntimeException ex) {
            System.out.println("Expected error when starting duplicate game: " + ex.getMessage());
        }

        scoreBoard.startGame(new Team("Spain"), new Team("Brazil"));
        scoreBoard.startGame(new Team("Germany"), new Team("France"));
        scoreBoard.startGame(new Team("Uruguay"), new Team("Italy"));
        scoreBoard.startGame(new Team("Argentina"), new Team("Australia"));

        printSummary(scoreBoard, "After starts (all games should be 0 - 0)");

        // Several updates (partial)
        scoreBoard.updateScore(new Team("Mexico"), new Team("Canada"), 0, 5);
        scoreBoard.updateScore(new Team("Spain"), new Team("Brazil"), 10, 2);

        printSummary(scoreBoard, "After several updates");

        // Remaining updates (complete the example)
        scoreBoard.updateScore(new Team("Germany"), new Team("France"), 2, 2);
        scoreBoard.updateScore(new Team("Uruguay"), new Team("Italy"), 6, 6);
        scoreBoard.updateScore(new Team("Argentina"), new Team("Australia"), 3, 1);

        printSummary(scoreBoard, "After all updates (final state)");

        // Finish a game (example)
        scoreBoard.finishGame(new Team("Germany"), new Team("France"));
        printSummary(scoreBoard, "After finishing Germany vs France");

        // Extra: demonstrate other invalid operations (expected failures)
        System.out.println();
        System.out.println("=== Expected failures (showing domain validations) ===");

        // Team validations
        expectFailure("Team name null", () -> new Team(null));
        expectFailure("Team name blank", () -> new Team("   "));

        // startGame validations
        expectFailure("Start game with same team", () ->
                scoreBoard.startGame(new Team("Poland"), new Team("Poland"))
        );

        // updateScore validations
        expectFailure("Update score for non-existent game", () ->
                scoreBoard.updateScore(new Team("Nonexistent"), new Team("Game"), 1, 1)
        );
        expectFailure("Update score with negative values", () ->
                scoreBoard.updateScore(new Team("Mexico"), new Team("Canada"), -1, 0)
        );

        // finishGame validations
        expectFailure("Finish non-existent game", () ->
                scoreBoard.finishGame(new Team("Germany"), new Team("France")) // already finished above
        );

        // Game validations (direct usage)
        expectFailure("Create Game with null teams", () ->
                new Game(null, new Team("X"), 1L)
        );
        expectFailure("Create Game with same teams", () ->
                new Game(new Team("X"), new Team("X"), 1L)
        );
    }

    private static void printSummary(ScoreBoard scoreBoard, String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");

        var summary = scoreBoard.getSummary();
        if (summary.isEmpty()) {
            System.out.println("(no games)");
            return;
        }

        summary.forEach(game -> System.out.printf(
                "%s %d - %s %d (total: %d)%n",
                game.getHomeTeam().getName(),
                game.getScore().getHome(),
                game.getAwayTeam().getName(),
                game.getScore().getAway(),
                game.totalScore()
        ));
    }

    private static void expectFailure(String label, Runnable action) {
        try {
            action.run();
            System.out.println("UNEXPECTED success: " + label);
        } catch (RuntimeException ex) {
            System.out.println("Expected error (" + label + "): "
                    + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }

    private WorldCupScoreBoardLibraryDemo() {
        // no instances
    }
}