package com.mikhailkarpov.coronavirustracker.repository;

import com.mikhailkarpov.coronavirustracker.dto.Report;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface ReportsRepository {

    void fetchData(LocalDate date) throws IOException;
    List<Report> getLastReports();
    LocalDate getLastUpdate();
}
