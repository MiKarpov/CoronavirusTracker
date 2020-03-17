package com.mikhailkarpov.coronavirustracker.repository;

import com.mikhailkarpov.coronavirustracker.dto.DailyReport;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface DailyReportsRepository {

    void fetchData(LocalDate date) throws IOException;
    Optional<Map<String, DailyReport>> getDailyReport(LocalDate date);
    Optional<Map<String, DailyReport>> getLastDailyReport();
    LocalDate getLastUpdate();
}
