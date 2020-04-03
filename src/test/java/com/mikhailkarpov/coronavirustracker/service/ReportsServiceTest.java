package com.mikhailkarpov.coronavirustracker.service;

import com.mikhailkarpov.coronavirustracker.dto.Report;
import com.mikhailkarpov.coronavirustracker.repository.ReportsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportsServiceTest {

    private static final LocalDate DATE = LocalDate.of(2020, 2, 20);
    private ReportsRepository mockRepository = mock(ReportsRepository.class);
    private ReportsService service;
    private List<Report> mockReports = mock(List.class);

    @BeforeEach
    public void setUp() {
        service = new ReportsService(mockRepository);
    }

    @Test
    public void testGetLastUpdate() {
        when(mockRepository.getLastUpdate()).thenReturn(DATE);

        assertEquals(DATE, service.getLastUpdate());
    }

    @Test
    public void testgetLastReports() {
        when(mockRepository.getLastReports()).thenReturn(mockReports);

        assertEquals(mockReports, service.getLastReports());
    }

    @Test
    public void testGetReports() {
        when(mockRepository.getLastReports()).thenReturn(mockReports);

        assertEquals(mockReports, service.getLastReports());
    }
}