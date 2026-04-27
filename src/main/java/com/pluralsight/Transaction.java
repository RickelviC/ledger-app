package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {
    private LocalDate dayOfTransactions;
    private LocalTime timeOfTransactions;
    private String description;
    private String vendor;
    private double amount;

    public Transaction(LocalDate dayOfTransactions, LocalTime timeOfTransactions, String description, String vendor, double amount) {
        this.dayOfTransactions = dayOfTransactions;
        this.timeOfTransactions = timeOfTransactions;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }


    public LocalDate getDayOfTransactions() {
        return dayOfTransactions;
    }

    public void setDayOfTransactions(LocalDate dayOfTransactions) {
        this.dayOfTransactions = dayOfTransactions;
    }

    public LocalTime getTimeOfTransactions() {
        return timeOfTransactions;
    }

    public void setTimeOfTransactions(LocalTime timeOfTransactions) {
        this.timeOfTransactions = timeOfTransactions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "dayOfTransactions: " + dayOfTransactions + " | timeOfTransactions: " + timeOfTransactions + " | description: " + description + " | vendor: " + vendor + " | amount : " + amount;
    }
}
