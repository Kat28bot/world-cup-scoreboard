package com.example.scoreboard.web;

import com.example.scoreboard.application.ScoreBoardFacade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ScoreBoardPageController {

    private final ScoreBoardFacade facade;

    public ScoreBoardPageController(ScoreBoardFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/")
    public String view(Model model) {
        model.addAttribute("games", facade.getSummary());
        return "scoreboard";
    }

    @PostMapping("/start")
    public String start(@RequestParam String homeTeam,
                        @RequestParam String awayTeam) {
        facade.startGame(homeTeam, awayTeam);
        return "redirect:/";
    }

    @PostMapping("/update")
    public String update(@RequestParam String homeTeam,
                         @RequestParam String awayTeam,
                         @RequestParam int homeScore,
                         @RequestParam int awayScore) {
        facade.updateScore(homeTeam, awayTeam, homeScore, awayScore);
        return "redirect:/";
    }

    @PostMapping("/finish")
    public String finish(@RequestParam String homeTeam,
                         @RequestParam String awayTeam) {
        facade.finishGame(homeTeam, awayTeam);
        return "redirect:/";
    }
}
