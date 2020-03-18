package com.mikhailkarpov.coronavirustracker.dto;

import java.time.LocalDate;

public class Report {

    private final String country;
    private final LocalDate date;
    private int confirmed;
    private int deaths;
    private int recovered;

    public Report(String country, LocalDate date) {
        this.country = country;
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setConfirmed(int confirmed) {
        if (confirmed < 0) {
            throw new IllegalArgumentException("Confirmed cases must be >= 0");
        }
        this.confirmed = confirmed;
    }

    public void setDeaths(int deaths) {
        if (deaths < 0) {
            throw new IllegalArgumentException("Amount of deaths must be >= 0");
        }
        this.deaths = deaths;
    }

    public void setRecovered(int recovered) {
        if (recovered < 0) {
            throw new IllegalArgumentException("Amount of recovered must be >= 0");
        }
        this.recovered = recovered;
    }
}
