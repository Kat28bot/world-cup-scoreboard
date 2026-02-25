package com.example.scoreboard.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class TeamTest {
    @Test
    void shouldCapitalizeFirstLetter() {
        Team team = new Team("poland");

        assertEquals("Poland", team.getName());
    }

    @Test
    void shouldTrimAndCapitalizeName() {
        Team team = new Team("  brazil  ");

        assertEquals("Brazil", team.getName());
    }

    @Test
    void shouldHandleSingleCharacter() {
        Team team = new Team("a");

        assertEquals("A", team.getName());
    }

    @Test
    void shouldKeepCapitalizedName() {
        Team team = new Team("Germany");

        assertEquals("Germany", team.getName());
    }

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

    @Test
    void hashCodeShouldMatchForEqualTeams() {
        Team t1 = new Team("Brazil");
        Team t2 = new Team("Brazil");
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void hashCodeShouldDifferForNonEqualTeams() {
        Team t1 = new Team("Brazil");
        Team t2 = new Team("Argentina");
        assertNotEquals(t1, t2);
        assertNotEquals(t1.hashCode(), t2.hashCode());
    }


    @Test
    void shouldNotBeEqualToNull() {
        Team team = new Team("Brazil");
        assertNotEquals(null, team);
    }

}
