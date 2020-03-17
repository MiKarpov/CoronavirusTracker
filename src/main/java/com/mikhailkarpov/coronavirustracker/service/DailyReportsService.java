package com.mikhailkarpov.coronavirustracker.service;

import com.mikhailkarpov.coronavirustracker.dto.DailyReport;
import com.mikhailkarpov.coronavirustracker.repository.DailyReportsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class DailyReportsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DailyReportsService.class);
    private final DailyReportsRepository repository;

    @Autowired
    public DailyReportsService(DailyReportsRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void fetchData() {
        LocalDate from = LocalDate.of(2020, 1, 22);
        LocalDate now = LocalDate.now();

        for (LocalDate date = from; !date.isAfter(now); date = date.plusDays(1)) {
            try {
                repository.fetchData(date);
            } catch (IOException e) {
                LOGGER.error("Fetching data failed for " + date, e);
            }
        }
        LOGGER.info("Data has been fetched");
    }

    @Scheduled(cron = "* * 1 * * *")
    private void updateData() {
        LocalDate now = LocalDate.now();
        try {
            repository.fetchData(now);
        } catch (IOException e) {
            LOGGER.error("Data not found for " + now);
        }
    }

    public Optional<Map<String, DailyReport>> getDailyReports(LocalDate date) {
        return repository.getDailyReport(date);
    }

    public Map<String, DailyReport> getLastReports() {
        Optional<Map<String, DailyReport>> lastReport = repository.getLastDailyReport();
        if (lastReport.isPresent()) {
            return lastReport.get();
        }
        LOGGER.error("Last report is absent");
        return null;
    }

    public LocalDate getLastUpdate() {
        return repository.getLastUpdate();
    }
}
