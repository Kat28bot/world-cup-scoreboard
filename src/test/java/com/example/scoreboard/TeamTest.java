package com.example.scoreboard;

import com.example.scoreboard.domain.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class TeamTest {

    @Test
    void shouldCreateTeamWithValidName() {
        Team team = new Team("Brazil");
        assertEquals("Brazil", team.getName());
    }

    @Test
    void shouldTrimWhitespaceFromName() {
        Team team = new Team("  Argentina  ");
        assertEquals("Argentina", team.getName());
    }

    @Test
    void shouldThrowIfNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Team("  "));
    }

    @Test
    void shouldThrowIfNameIsNull() {
        assertThrows(NullPointerException.class, () -> new Team(null));
    }

    @Test
    void equalityBasedOnName() {
        Team a = new Team("Mexico");
        Team b = new Team("Mexico ");
        Team c = new Team("Canada");
        assertEquals(a, b);
        assertNotEquals(a, c);
    }
}
