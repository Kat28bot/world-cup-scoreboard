package com.example.scoreboard.domain;


import lombok.Value;


@Value
public class Team {
         String name;

        public Team(String name) {
            if (name == null) throw new NullPointerException("Team name cannot be null");
            if (name.isBlank()) throw new IllegalArgumentException("Team name cannot be blank");
            this.name = name.trim();
        }
}
