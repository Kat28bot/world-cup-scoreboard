package com.example.scoreboard.domain;

import lombok.Value;

@Value
public class Score {
    int home;
    int away;

    public Score(int home, int away) {
        if (home < 0 || away < 0)
            throw new IllegalArgumentException("Score cannot be negative");
        this.home = home;
        this.away = away;
    }

    public int total() {
        return home + away;
    }
}
