package com.example.scoreboardweb.web;

import com.example.scoreboardweb.application.ScoreBoardFacade;
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
                       @ModelAttribute("startError") String startError,
                       @ModelAttribute("updateError") String updateError,
                       @ModelAttribute("finishError") String finishError) {
        model.addAttribute("games", facade.getSummary());
        model.addAttribute("startError", startError);
        model.addAttribute("updateError", updateError);
        model.addAttribute("finishError", finishError);
        return "scoreboard";
    }

    @PostMapping("/start")
    public String start(@RequestParam String homeTeam,
                        @RequestParam String awayTeam,
                        RedirectAttributes redirect) {

        try {
            facade.startGame(homeTeam, awayTeam);
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("startError", ex.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/update")
    public String update(@RequestParam String homeTeam,
                         @RequestParam String awayTeam,
                         @RequestParam int homeScore,
                         @RequestParam int awayScore,
                         RedirectAttributes redirectAttributes) {

        try {
            facade.updateScore(homeTeam, awayTeam, homeScore, awayScore);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("updateError", ex.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/finish")
    public String finish(@RequestParam String homeTeam,
                         @RequestParam String awayTeam,
                         RedirectAttributes redirect) {
        try {
            facade.finishGame(homeTeam, awayTeam);
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("finishError", ex.getMessage());
        }
        return "redirect:/";
    }
}
