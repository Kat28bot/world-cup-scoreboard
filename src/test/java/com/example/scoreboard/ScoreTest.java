package com.example.scoreboard;

import com.example.scoreboard.domain.Score;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreTest {
    @Test
    void shouldCreateScoreWithValidValues() {
        Score score = new Score(2, 3);
        assertEquals(2, score.getHome());
        assertEquals(3, score.getAway());
        assertEquals(5, score.total());
    }
    @Test
    void shouldCalculateTotalScore() {
        Score score = new Score(2, 3);
        assertEquals(5, score.total());
    }
    @Test
    void shouldAllowZeroScores() {
        Score score = new Score(0, 0);
        assertEquals(0, score.getHome());
        assertEquals(0, score.getAway());
        assertEquals(0, score.total());
    }

    @Test
    void shouldHandleLargeScores() {
        Score score = new Score(1000, 2000);
        assertEquals(1000, score.getHome());
        assertEquals(2000, score.getAway());
        assertEquals(3000, score.total());
    }
    @Test
    void shouldThrowIfScoreIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Score(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Score(0, -2));
    }

    @Test
    void equalityBasedOnValues() {
        Score s1 = new Score(1, 2);
        Score s2 = new Score(1, 2);
        Score s3 = new Score(2, 1);
        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
    }
    @Test
    void equalityWithZeroScores() {
        Score zero1 = new Score(0, 0);
        Score zero2 = new Score(0, 0);
        assertEquals(zero1, zero2);
    }
}
