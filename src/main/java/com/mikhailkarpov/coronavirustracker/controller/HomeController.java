package com.mikhailkarpov.coronavirustracker.controller;

import com.mikhailkarpov.coronavirustracker.dto.Report;
import com.mikhailkarpov.coronavirustracker.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private static final String SORT_BY_COUNTRY = "country";
    private static final String SORT_BY_CONFIRMED = "confirmed";
    private static final String SORT_BY_RECOVERED = "recovered";
    private static final String SORT_BY_DEATHS = "deaths";
    private static final String ORDER_ASCENDING = "asc";
    private static final String ORDER_DESCENDING = "desc";

    private final ReportsService service;
    private final Comparator<Report> comparatorByConfirmed = Comparator.comparingInt(Report::getConfirmed).reversed();


    @Autowired
    public HomeController(ReportsService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String homePage(@RequestParam Optional<String> sortBy, Model model) {
        Comparator<Report> comparator = getComparator(sortBy);

        List<Report> lastReports = service
                .getLastReports()
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());

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

    private Comparator<Report> getComparator(Optional<String> sortBy) {
        Comparator<Report> comparator = Comparator.comparing(Report::getConfirmed).reversed();

        if (!sortBy.isPresent())
            return comparator;

        switch (sortBy.get()) {
            case (SORT_BY_COUNTRY):
                comparator = Comparator.comparing(Report::getConfirmed);
                break;
            case (SORT_BY_RECOVERED):
                comparator = Comparator.comparing(Report::getRecovered).reversed();
                break;
            case (SORT_BY_DEATHS):
                comparator = Comparator.comparing(Report::getDeaths).reversed();
                break;
        }

        return comparator;
    }
}
