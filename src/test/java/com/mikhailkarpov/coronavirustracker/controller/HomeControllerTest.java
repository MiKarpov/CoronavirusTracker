package com.mikhailkarpov.coronavirustracker.controller;

import com.mikhailkarpov.coronavirustracker.dto.Report;
import com.mikhailkarpov.coronavirustracker.service.ReportsService;
import com.sun.tools.javac.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReportsService service;
    @Autowired
    private HomeController controller;

    @Test
    public void contextLoads() {
        assertNotNull(controller);
    }

    @Test
    public void shouldReturnHomePage() throws Exception {
        LocalDate now = LocalDate.now();

        Report report1 = new Report("China", now);
        report1.setConfirmed(11);
        report1.setDeaths(12);
        report1.setRecovered(13);

        Report report2 = new Report("US", now);
        report2.setConfirmed(21);
        report2.setDeaths(22);
        report2.setRecovered(23);

        List<Report> reports = List.of(report1, report2);

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