package com.mikhailkarpov.coronavirustracker.repository;

import com.mikhailkarpov.coronavirustracker.dto.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportsRepositoryImplTest {

    private static final LocalDate DATE = LocalDate.of(2020, 3, 16);
    private ReportsRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ReportsRepositoryImpl();
    }

    @Test
    public void constructorShouldCreateEmptyRepository() {
        assertNull(repository.getLastUpdate());
        assertTrue(repository.getReports(LocalDate.now()).isEmpty());
        assertTrue(repository.getLastReports().isEmpty());
    }

    @Test
    public void testFetchData() throws IOException {
        repository.fetchData(DATE);
        assertEquals(DATE, repository.getLastUpdate());
        
        List<Report> lastReports = repository.getLastReports();

        assertFalse(lastReports.isEmpty());
        for (Report report : lastReports) {
            assertEquals(DATE, report.getDate());
        }

        List<Report> reports = repository.getReports(DATE);
        
        assertFalse(reports.isEmpty());
        for (Report report : reports) {
            assertEquals(DATE, report.getDate());
        }
        assertEquals(181546, totalConfirmed(reports));
        assertEquals(7126, totalDeaths(reports));
        assertEquals(78088, totalRecovered(reports));
    }

    @Test
    public void testMultipleFetchData() throws IOException {
        repository.fetchData(DATE);
        repository.fetchData(DATE.plusDays(1));
        
        assertEquals(DATE.plusDays(1), repository.getLastUpdate());

        List<Report> lastReports = repository.getLastReports();
        for (Report report : lastReports) {
            assertEquals(DATE.plusDays(1), report.getDate());
        }
        assertEquals(197168, totalConfirmed(lastReports));
        assertEquals(7905, totalDeaths(lastReports));
        assertEquals(80840, totalRecovered(lastReports));
    }
    
    private int totalConfirmed(List<Report> reports) {
        return reports.stream().mapToInt(Report::getConfirmed).sum();
    }

    private int totalDeaths(List<Report> reports) {
        return reports.stream().mapToInt(Report::getDeaths).sum();
    }

    private int totalRecovered(List<Report> reports) {
        return reports.stream().mapToInt(Report::getRecovered).sum();
    }
}