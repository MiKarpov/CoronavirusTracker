package com.mikhailkarpov.coronavirustracker.repository;

import com.mikhailkarpov.coronavirustracker.dto.Report;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReportsRepositoryImpl implements ReportsRepository {

    private static final String URL_PATTERN = "https://github.com/CSSEGISandData/COVID-19/raw/master/csse_covid_19_data/csse_covid_19_daily_reports/";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportsRepositoryImpl.class);

    private LocalDate lastUpdate;
    private Map<LocalDate, Map<String, Report>> reportsPerCountryPerDate = new ConcurrentHashMap<>();

    @Override
    public void fetchData(LocalDate date) throws IOException {
        if (reportsPerCountryPerDate.containsKey(date)) {
            LOGGER.debug("Report has already been fetched");
            return;
        }

        String url = URL_PATTERN + DATE_TIME_FORMATTER.format(date) + ".csv";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            LOGGER.info("Parsing data for " + date);

            CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withHeader());
            Map<String, Report> reportPerCountry = new HashMap<>();
            for (CSVRecord record : parser) {
                saveRecord(date, record, reportPerCountry);
            }
            reportsPerCountryPerDate.put(date, reportPerCountry);

            if (lastUpdate == null || lastUpdate.isBefore(date)) lastUpdate = date;
        }
    }

    private void saveRecord(LocalDate date, CSVRecord record, Map<String, Report> fetchedReports) {
        try {
            String country = record.get("Country/Region");
            int confirmed = parseRecord(record, "Confirmed");
            int deaths = parseRecord(record, "Deaths");
            int recovered = parseRecord(record, "Recovered");
            LOGGER.debug("New record: " + country + "(confirmed, deaths, recovered):" + confirmed +", " + deaths + ", " + recovered);

            Report report = fetchedReports.get(country);
            if (report != null) { // Data has been previously parsed for this country, but different province
                confirmed += report.getConfirmed();
                deaths += report.getDeaths();
                recovered += report.getRecovered();
            } else {
                report = new Report(country, date);
            }
            report.setConfirmed(confirmed);
            report.setDeaths(deaths);
            report.setRecovered(recovered);

            fetchedReports.put(country, report);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Failed to parse record: " + record);
        }
    }

    private int parseRecord(CSVRecord record, String header) {
        String value = record.get(header);
        if (value == null) {
            LOGGER.debug("No value is present in record {} and header {} ", record, header);
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.debug("Can't parse Integer from " + value);
            return 0;
        }
    }

    @Override
    public List<Report> getReports(LocalDate date) {
        Map<String, Report> reportsPerCountry = reportsPerCountryPerDate.get(date);
        if (reportsPerCountry == null) {
            return Collections.emptyList();
        }
        Collection<Report> reports = reportsPerCountry.values();
        return Collections.unmodifiableList(new ArrayList<>(reports));
    }

    @Override
    public List<Report> getLastReports() {
        if (lastUpdate == null) {
            return Collections.emptyList();
        } else {
            return getReports(lastUpdate);
        }
    }

    @Override
    public LocalDate getLastUpdate() {
        return lastUpdate;
    }
}