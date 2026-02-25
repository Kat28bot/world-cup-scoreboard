package com.example.scoreboard.domain;

import lombok.Value;

@Value
public class Team {
    String name;

    public Team(String name) {
        if (name == null) throw new NullPointerException("Team name cannot be null");
        name = name.trim();
        if (name.isBlank()) throw new IllegalArgumentException("Team name cannot be blank");

        name = name.toLowerCase();
        this.name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
