package com.mikhailkarpov.coronavirustracker.service;

import com.mikhailkarpov.coronavirustracker.dto.Report;
import com.mikhailkarpov.coronavirustracker.repository.ReportsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportsService.class);
    private final ReportsRepository repository;

    @Autowired
    public ReportsService(ReportsRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void fetchData() {
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.of(2020, 1, 22);

        for (LocalDate date = from; !date.isBefore(to); date = date.minusDays(1)) {
            LocalDate lastUpdate = repository.getLastUpdate();
            if (lastUpdate != null)
                return;

            try {
                repository.fetchData(date);
                LOGGER.info("Data has been fetched. Last update: " + date);
                return;
            } catch (IOException e) {
                LOGGER.warn("Fetching data failed for {}: {}", date, e.getMessage());
            }
        }
    }

    @Scheduled(cron = "* * 1 * * *")
    private void updateData() {
        LocalDate now = LocalDate.now();
        try {
            repository.fetchData(now);
            LOGGER.info("Updated on {}", LocalDateTime.now());
        } catch (IOException e) {
            LOGGER.debug("Update failed. Data not found for " + now);
        }
    }

    public List<Report> getLastReports() {
        return repository.getLastReports();
    }

    public LocalDate getLastUpdate() {
        return repository.getLastUpdate();
    }
}