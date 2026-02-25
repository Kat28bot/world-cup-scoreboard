package com.example.scoreboard.web;

import com.example.scoreboard.application.ScoreBoardFacade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ScoreBoardPageController {

    private final ScoreBoardFacade facade;

    public ScoreBoardPageController(ScoreBoardFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/")
    public String view(Model model,
                       @ModelAttribute("startError") String startError) {
        model.addAttribute("games", facade.getSummary());
        model.addAttribute("startError", startError); // display fallback message if any
        return "scoreboard";
    }

    @PostMapping("/start")
    public String start(@RequestParam String homeTeam,
                        @RequestParam String awayTeam,
                        RedirectAttributes redirect) {

        ScoreBoardFacade.StartGameResult result = facade.startGame(homeTeam, awayTeam);

        if (!result.success()) {
            redirect.addFlashAttribute("startError", result.message());
        }

        return "redirect:/";
    }

    @PostMapping("/update")
    public String update(@RequestParam String homeTeam,
                         @RequestParam String awayTeam,
                         @RequestParam int homeScore,
                         @RequestParam int awayScore,
                         RedirectAttributes redirectAttributes) {

        ScoreBoardFacade.UpdateScoreResult result =
                facade.updateScore(homeTeam, awayTeam, homeScore, awayScore);

        if (!result.success()) {
            redirectAttributes.addFlashAttribute("updateError", result.message());
        }

        return "redirect:/";
    }

    @PostMapping("/finish")
    public String finish(@RequestParam String homeTeam,
                         @RequestParam String awayTeam) {
        facade.finishGame(homeTeam, awayTeam);
        return "redirect:/";
    }
}
