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

@Component
public class ReportsRepositoryImpl implements ReportsRepository {

    private static final String URL_PATTERN = "https://github.com/CSSEGISandData/COVID-19/raw/master/csse_covid_19_data/csse_covid_19_daily_reports/";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportsRepositoryImpl.class);

    private LocalDate lastUpdate;
    private List<Report> lastReports;

    @Override
    public void fetchData(LocalDate date) throws IOException {
        String url = URL_PATTERN + DATE_TIME_FORMATTER.format(date) + ".csv";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            LOGGER.info("Parsing data for {} from {}", date, url);
            CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withHeader());

            Map<String, Report> reportPerCountry = new HashMap<>();
            for (CSVRecord record : parser) {
                parseAndSaveReport(record, date, reportPerCountry);
            }
            lastReports = Collections.synchronizedList(new ArrayList<>(reportPerCountry.values()));

            if (lastUpdate == null || lastUpdate.isBefore(date)) lastUpdate = date;
        }
    }

    private void parseAndSaveReport(CSVRecord record, LocalDate date, Map<String, Report> reportPerCountry) {
        try {
            // Parse data
            String country = record.get(3);
            int confirmed = parseRecord(record, "Confirmed");
            int deaths = parseRecord(record, "Deaths");
            int recovered = parseRecord(record, "Recovered");

            // Create or update record
            Report report = reportPerCountry.get(country);
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

            //Save or update record
            reportPerCountry.put(country, report);

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
    public List<Report> getLastReports() {
        if (lastReports == null || lastReports.isEmpty())
            return Collections.emptyList();
        else
            return lastReports;
    }

    @Override
    public LocalDate getLastUpdate() {
        return lastUpdate;
    }
}