package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount; a payment is stored
 * as a negative amount.
 */
public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    /**
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     */
    public static void loadTransactions(String fileName) {
        String line;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));

            while ((line = reader.readLine()) != null) {

                String[] divider = line.split("\\|");
                LocalDate dayOfTransactions = LocalDate.parse(divider[0]);
                LocalTime timeOfTransactions = LocalTime.parse(divider[1]);
                String description = divider[2];
                String vendor = divider[3];
                double amount = Double.parseDouble(divider[4]);

                Transaction transaction = new Transaction(dayOfTransactions, timeOfTransactions, description, vendor, amount);
                transactions.add(transaction);

            }
        } catch (Exception ex) {
            System.err.println("Something went wrong");
        }
        // TODO: create file if it does not exist, then read each line,
        //       parse the five fields, build a Transaction object,
        //       and add it to the transactions list.
    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
    private static void addDeposit(Scanner scanner) {

        try {
            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));

            System.out.print("Enter date of transaction (yyyy-MM-dd): ");
            String inputDate = scanner.nextLine().trim();
            LocalDate date = LocalDate.parse(inputDate, DATE_FMT);

            /*String inputDate = "";

            while (!inputDate.equals(String.valueOf(DATE_FMT))){
                System.out.println("please Enter date with this format (yyyy-MM-dd): ");
                inputDate = scanner.nextLine().trim();

            }*/

            System.out.print("Enter time of transaction (HH:mm:ss): ");
            String inputTime = scanner.nextLine().trim();
            LocalTime time = LocalTime.parse(inputTime, TIME_FMT);

            System.out.print("Enter description of transaction: ");
            String inputDescription = scanner.nextLine().trim();

            System.out.print("Enter vendor of transaction: ");
            String inputVendor = scanner.nextLine().trim();

            System.out.print("Enter amount of transaction: ");
            double inputAmount = scanner.nextDouble();
            double amount = Math.abs(inputAmount);
            scanner.nextLine();

            Transaction transaction = new Transaction(date, time, inputDescription, inputVendor, amount);
            transactions.add(transaction);

            buffWriter.write(date + "|" + time + "|" + inputDescription + "|" + inputVendor + "|" + amount + "\n");
            buffWriter.close();

        } catch (Exception ex) {
            System.err.println("something went wrong!!!");
        }


        // TODO
    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
    private static void addPayment(Scanner scanner) {
        try {
            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));

            System.out.print("Enter date of transaction (yyyy-MM-dd): ");
            String inputDate = scanner.nextLine().trim();
            LocalDate date = parseDate(inputDate);

            System.out.print("Enter time of transaction (HH:mm:ss): ");
            String inputTime = scanner.nextLine().trim();
            LocalTime time = LocalTime.parse(inputTime, TIME_FMT);

            System.out.print("Enter description of transaction: ");
            String inputDescription = scanner.nextLine().trim();

            System.out.print("Enter vendor of transaction: ");
            String inputVendor = scanner.nextLine().trim();

            System.out.print("Enter amount of transaction: ");
            double inputAmount = scanner.nextDouble();
            double amount = -Math.abs(inputAmount);
            scanner.nextLine();

            Transaction transaction = new Transaction(date, time, inputDescription, inputVendor, amount);
            transactions.add(transaction);

            buffWriter.write(date + "|" + time + "|" + inputDescription + "|" + inputVendor + "|" + amount + "\n");
            buffWriter.close();

        } catch (Exception ex) {
            System.err.println("something went wrong!!!");
        }
        // TODO
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() {

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }

        /* TODO – print all transactions in column format */
    }

    private static void displayDeposits() {
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                System.out.println(transaction);
            }
        }
        /* TODO – only amount > 0               */
    }

    private static void displayPayments() {
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.println(transaction);
            }
            /* TODO – only amount < 0               */
        }
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    filterTransactionsByDate(LocalDate.now().withDayOfMonth(1), LocalDate.now());
                    /* TODO – month-to-date report */
                }
                case "2" -> {

                    LocalDate firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                    LocalDate lastDayLastOfMonth = LocalDate.now().withDayOfMonth(1).minusDays(1);

                    filterTransactionsByDate(firstDayOfLastMonth, lastDayLastOfMonth);
                    /* TODO – previous month report */
                }
                case "3" -> {
                    LocalDate firstDayOfYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());

                    filterTransactionsByDate(firstDayOfYear, LocalDate.now());

                    /* TODO – year-to-date report   */
                }
                case "4" -> {
                    LocalDate firstDayOfLastYear = LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear());
                    LocalDate lastDayOfLastYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).minusDays(1);

                    filterTransactionsByDate(firstDayOfLastYear, lastDayOfLastYear);

                    /* TODO – previous year report  */
                }
                case "5" -> {
                    System.out.println("Enter name of vendor");
                    String vendor = scanner.nextLine();

                    filterTransactionsByVendor(vendor);
                    /* TODO – prompt for vendor then report */
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        for (Transaction transaction : transactions) {
            if (transaction.getDayOfTransactions().isBefore(end) && transaction.getDayOfTransactions().isAfter(start)) {
                System.out.println(transaction);
            }
        }
        // TODO – iterate transactions, print those within the range
    }

    private static void filterTransactionsByVendor(String vendor) {
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                System.out.println(transaction);
            }
        }
        // TODO – iterate transactions, print those with matching vendor
    }

    private static void customSearch(Scanner scanner) {

        System.out.println("Enter starting Date (yyyy-MM-dd): ");
        String start = scanner.nextLine();
        LocalDate startingDate = null;

        if (start.isEmpty()) {
            startingDate = parseDate(start);
        }

        System.out.println("Enter ending Date (yyyy-MM-dd): ");
        String end = scanner.nextLine();

        LocalDate endingDate = null;

        if (end.isEmpty()) {
            startingDate = parseDate(start);
        }
        // TODO – prompt for any combination of date range, description,
        //        vendor, and exact amount, then display matches
    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {

        try {
            return LocalDate.parse(s, DATE_FMT);
            /* TODO – return LocalDate or null */
        } catch (Exception ex) {
            return null;
        }
    }

    private static Double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
            /* TODO – return Double or null */
        } catch (Exception ex) {
            return null;
        }
    }
}
