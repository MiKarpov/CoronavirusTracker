package com.mikhailkarpov.coronavirustracker.controller;

import com.mikhailkarpov.coronavirustracker.dto.Report;
import com.mikhailkarpov.coronavirustracker.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ReportsService service;

    @Autowired
    public HomeController(ReportsService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        List<Report> lastReports = service.getLastReports();

        int totalConfirmed = lastReports.stream().mapToInt(Report::getConfirmed).sum();
        int totalDeaths = lastReports.stream().mapToInt(Report::getDeaths).sum();
        int totalRecovered = lastReports.stream().mapToInt(Report::getRecovered).sum();

        model.addAttribute("totalConfirmed", totalConfirmed);
        model.addAttribute("totalDeaths", totalDeaths);
        model.addAttribute("totalRecovered", totalRecovered);
        model.addAttribute("reports", lastReports);
        model.addAttribute("lastUpdate", service.getLastUpdate());

        return "latestReport";
    }
}
