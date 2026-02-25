package com.example.scoreboard.domain;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class Game {
    @NonNull
    private final Team homeTeam;
    @NonNull
    private final Team awayTeam;
    private final long startOrder;
    private Score score;

    public Game(@NonNull Team homeTeam, @NonNull Team awayTeam, long startOrder) {
        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("Home and Away teams cannot be the same");
        }
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.startOrder = startOrder;
        this.score = new Score(0,0);
    }

    public void updateScore(int homeScore, int awayScore) {
        this.score = new Score(homeScore, awayScore);
    }

    public int totalScore() {
        return score.total();
    }

    @Override
    public String toString() {
        return homeTeam + " " + score.getHome() + "-" + score.getAway() + " " + awayTeam;
    }
}
