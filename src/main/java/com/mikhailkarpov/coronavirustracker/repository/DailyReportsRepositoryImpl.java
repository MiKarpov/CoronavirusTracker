package com.mikhailkarpov.coronavirustracker.repository;

import com.mikhailkarpov.coronavirustracker.dto.DailyReport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DailyReportsRepositoryImpl implements DailyReportsRepository {

    private static final String URL_PATTERN = "https://github.com/CSSEGISandData/COVID-19/raw/master/csse_covid_19_data/csse_covid_19_daily_reports/";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyReportsRepositoryImpl.class);

    private LocalDate lastUpdate;
    private Map<LocalDate, Map<String, DailyReport>> dailyReports = new ConcurrentHashMap<>();

    @Override
    public void fetchData(LocalDate date) throws IOException {
        String url = URL_PATTERN + DATE_TIME_FORMATTER.format(date) + ".csv";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            LOGGER.info("Parsing data for " + date);

            CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withHeader());
            Map<String, DailyReport> reportPerCountry = new HashMap<>();
            for (CSVRecord record : parser) {
                createOrUpdateReport(reportPerCountry, record);
            }
            dailyReports.put(date, reportPerCountry);

            if (lastUpdate == null || lastUpdate.isBefore(date)) lastUpdate = date;
        }
    }

    private void createOrUpdateReport(Map<String, DailyReport> reportPerCountry, CSVRecord record) {
        try {
            String country = record.get("Country/Region");
            int confirmed = Integer.parseInt(record.get("Confirmed"));
            int deaths = Integer.parseInt(record.get("Deaths"));
            int recovered = Integer.parseInt(record.get("Recovered"));
            LOGGER.debug("New record: " + country + "(confirmed, deaths, recovered):" + confirmed +", " + deaths + ", " + recovered);

            DailyReport report = reportPerCountry.get(country);

            if (report != null) { // Data has been previously parsed for this country, but different province
                confirmed += report.getConfirmed();
                deaths += report.getDeaths();
                recovered += report.getRecovered();
            } else {
                report = new DailyReport();
            }

            report.setConfirmed(confirmed);
            report.setDeaths(deaths);
            report.setRecovered(recovered);

            reportPerCountry.put(country, report);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Failed to parse record: " + record);
        }
    }

    @Override
    public Optional<Map<String, DailyReport>> getDailyReport(LocalDate date) {
        return Optional.ofNullable(dailyReports.get(date));
    }

    @Override
    public Optional<Map<String, DailyReport>> getLastDailyReport() {
        if (lastUpdate == null) return Optional.empty();
        return Optional.ofNullable(dailyReports.get(lastUpdate));
    }

    @Override
    public LocalDate getLastUpdate() {
        return lastUpdate;
    }
}