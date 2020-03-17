package com.mikhailkarpov.coronavirustracker.repository;

import com.mikhailkarpov.coronavirustracker.dto.DailyReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DailyReportsRepositoryImplTest {

    private DailyReportsRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DailyReportsRepositoryImpl();
    }

    @Test
    public void constructorShouldCreateEmptyRepository() {
        assertNull(repository.getLastUpdate());
        assertFalse(repository.getDailyReport(LocalDate.now()).isPresent());
        assertFalse(repository.getLastDailyReport().isPresent());
    }

    @Test
    public void testGetLastDailyReport() throws IOException {
        LocalDate date = LocalDate.now().minusDays(1);
        repository.fetchData(date);

        assertEquals(date, repository.getLastUpdate());
        assertTrue(repository.getLastDailyReport().isPresent());

        Optional<Map<String, DailyReport>> reports = repository.getDailyReport(date);
        assertTrue(reports.isPresent());

        Map<String, DailyReport> reportPerCountry = reports.get();
        assertTrue(reportPerCountry.containsKey("China"));
        assertTrue(reportPerCountry.containsKey("US"));
    }

    @Test
    public void testGetDailyReport() throws IOException {
        LocalDate date = LocalDate.of(2020, 3, 16);
        repository.fetchData(date);
        Optional<Map<String, DailyReport>> dailyReport = repository.getDailyReport(date);

        assertEquals(date, repository.getLastUpdate());
        assertTrue(dailyReport.isPresent());

        Collection<DailyReport> reports = dailyReport.get().values();
        int totalConfirmed = reports.stream().mapToInt(DailyReport::getConfirmed).sum();
        int totalDeaths = reports.stream().mapToInt(DailyReport::getDeaths).sum();
        int totalRecovered = reports.stream().mapToInt(DailyReport::getRecovered).sum();

        assertEquals(181546, totalConfirmed);
        assertEquals(7126, totalDeaths);
        assertEquals(78088, totalRecovered);

        DailyReport reportFromChina = dailyReport.get().get("China");

        assertEquals(81033, reportFromChina.getConfirmed());
        assertEquals(3217, reportFromChina.getDeaths());
        assertEquals(67910, reportFromChina.getRecovered());
    }
}