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
    private static final String ORDER_ASC = "asc";
    private static final String ORDER_DESC = "desc";

    private final ReportsService service;
    private final Comparator<Report> comparatorByConfirmed = Comparator.comparingInt(Report::getConfirmed).reversed();


    @Autowired
    public HomeController(ReportsService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String homePage(@RequestParam Optional<String> sortBy, @RequestParam Optional<String> order, Model model) {
        Comparator<Report> comparator = sortBy.isPresent() ? getComparator(sortBy.get(), order) :
                Comparator.comparing(Report::getConfirmed).reversed();

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

        if (sortBy.isPresent()) {
            model.addAttribute("sortBy", sortBy.get());
        } else {
            model.addAttribute("sortBy", SORT_BY_CONFIRMED);
        }

        if (order.isPresent() && order.get().equals(ORDER_ASC)) {
            model.addAttribute("order", ORDER_ASC);
        } else {
            model.addAttribute("order", ORDER_DESC);
        }

        return "latestReport";
    }

    private Comparator<Report> getComparator(String sortBy, Optional<String> order) {
        Comparator<Report> comparator;

        switch (sortBy) {
            case (SORT_BY_COUNTRY):
                comparator = Comparator.comparing(Report::getCountry);
                break;
            case (SORT_BY_RECOVERED):
                comparator = Comparator.comparing(Report::getRecovered);
                break;
            case (SORT_BY_DEATHS):
                comparator = Comparator.comparing(Report::getDeaths);
                break;
            default:
                comparator = Comparator.comparing(Report::getConfirmed);
                break;
        }

        if (order.isPresent() && order.get().equals(ORDER_DESC))
            comparator = comparator.reversed();

        return comparator;
    }
}
