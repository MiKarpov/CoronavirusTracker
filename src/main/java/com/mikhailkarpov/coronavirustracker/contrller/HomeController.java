package com.mikhailkarpov.coronavirustracker.contrller;

import com.mikhailkarpov.coronavirustracker.dto.DailyReport;
import com.mikhailkarpov.coronavirustracker.service.DailyReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.Map;

@Controller
public class HomeController {

    private final DailyReportsService service;

    @Autowired
    public HomeController(DailyReportsService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        Map<String, DailyReport> reportPerCountry = service.getLastReports();

        Collection<DailyReport> reports = reportPerCountry.values();
        int totalConfirmed = reports.stream().mapToInt(DailyReport::getConfirmed).sum();
        int totalDeaths = reports.stream().mapToInt(DailyReport::getDeaths).sum();
        int totalRecovered = reports.stream().mapToInt(DailyReport::getRecovered).sum();

        model.addAttribute("totalConfirmed", totalConfirmed);
        model.addAttribute("totalDeaths", totalDeaths);
        model.addAttribute("totalRecovered", totalRecovered);
        model.addAttribute("reports", reportPerCountry);
        model.addAttribute("lastUpdate", service.getLastUpdate());

        return "latestReport";
    }
}
