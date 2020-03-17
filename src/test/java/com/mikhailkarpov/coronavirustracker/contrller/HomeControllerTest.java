package com.mikhailkarpov.coronavirustracker.contrller;

import com.mikhailkarpov.coronavirustracker.dto.DailyReport;
import com.mikhailkarpov.coronavirustracker.service.DailyReportsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DailyReportsService service;
    @Autowired
    private HomeController controller;

    @Test
    public void contextLoads() {
        assertNotNull(controller);
    }

    @Test
    public void shouldReturnHomePage() throws Exception {
        DailyReport report1 = new DailyReport();
        report1.setConfirmed(11);
        report1.setDeaths(12);
        report1.setRecovered(13);

        DailyReport report2 = new DailyReport();
        report2.setConfirmed(21);
        report2.setDeaths(22);
        report2.setRecovered(23);

        Map<String, DailyReport> reports = new HashMap<>();
        reports.put("China", report1);
        reports.put("US", report2);

        LocalDate now = LocalDate.now();
        when(service.getLastUpdate()).thenReturn(now);
        when(service.getLastReports()).thenReturn(reports);

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("latestReport"))
                .andExpect(model().attribute("totalConfirmed", 32))
                .andExpect(model().attribute("totalDeaths",34))
                .andExpect(model().attribute("totalRecovered", 36))
                .andExpect(model().attribute("lastUpdate", now))
                .andExpect(model().attribute("reports", reports));

        verify(service, times(1)).getLastReports();
        verify(service, times(1)).getLastUpdate();
        verifyNoMoreInteractions(service);
    }
}