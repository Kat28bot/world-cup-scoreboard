package com.example.scoreboard;

import com.example.scoreboard.domain.Game;
import com.example.scoreboard.domain.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class GameTest {

    private Team brazil;
    private Team argentina;

    @BeforeEach
    void setup() {
        brazil = new Team("Brazil");
        argentina = new Team("Argentina");
    }

    @Test
    void shouldCreateGameWithValidTeamsAndInitialScore() {
        Game game = new Game(brazil, argentina,1);
        assertEquals(brazil, game.getHomeTeam());
        assertEquals(argentina, game.getAwayTeam());
        assertEquals(0, game.getScore().getHome());
        assertEquals(0, game.getScore().getAway());
        assertEquals(1, game.getStartOrder());
        assertEquals(0, game.getScore().total());
    }

    @Test
    void shouldThrowIfTeamsAreNull() {
        assertThrows(NullPointerException.class, () -> new Game(null, argentina,any()));
        assertThrows(NullPointerException.class, () -> new Game(brazil, null,any()));
    }

    @Test
    void shouldThrowIfTeamsAreTheSame() {
        assertThrows(IllegalArgumentException.class,
                () -> new Game(brazil, brazil, 1L));
    }

    @Test
    void shouldUpdateScoreCorrectly() {
        Game game = new Game(brazil, argentina, 1L);

        game.updateScore(2, 3);

        assertEquals(2, game.getScore().getHome());
        assertEquals(3, game.getScore().getAway());
        assertEquals(5, game.totalScore());
    }

    @Test
    void shouldThrowIfUpdatedScoreIsNegative() {
        Game game = new Game(brazil, argentina,1);
        assertThrows(IllegalArgumentException.class, () -> game.updateScore(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> game.updateScore(0, -2));
    }
}
