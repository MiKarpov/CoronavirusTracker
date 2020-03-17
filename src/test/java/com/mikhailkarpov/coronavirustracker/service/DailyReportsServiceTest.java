package com.mikhailkarpov.coronavirustracker.service;

import com.mikhailkarpov.coronavirustracker.dto.DailyReport;
import com.mikhailkarpov.coronavirustracker.repository.DailyReportsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DailyReportsServiceTest {

    private DailyReportsRepository mockRepository = mock(DailyReportsRepository.class);
    private DailyReportsService service;
    private Map<String, DailyReport> mockReports = mock(Map.class);

    @BeforeEach
    public void setUp() {
        service = new DailyReportsService(mockRepository);
    }

    @Test
    public void testGetLastUpdate() throws IOException {
        LocalDate now = LocalDate.now();
        when(mockRepository.getLastUpdate()).thenReturn(now);

        LocalDate lastUpdate = service.getLastUpdate();

        assertEquals(now, lastUpdate);
    }

    @Test
    public void whenGetLastReportsThenReturnLastReports() {
        when(mockRepository.getLastDailyReport()).thenReturn(Optional.of(mockReports));

        Map<String, DailyReport> actualReports = service.getLastReports();

        assertEquals(mockReports, actualReports);
    }

    @Test
    public void whenGetDailyReportThenReturn() {
        LocalDate now = LocalDate.now();
        when(mockRepository.getDailyReport(now)).thenReturn(Optional.of(mockReports));

        Optional<Map<String, DailyReport>> reports = service.getDailyReports(now);
        assertTrue(reports.isPresent());
        assertEquals(mockReports, reports.get());
    }
}