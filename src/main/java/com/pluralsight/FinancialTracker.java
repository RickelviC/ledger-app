package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
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
    private static final File FILE_NAME = new File("transactions.csv");
    //private static final String FILE_NAME = "transactions.csv";

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    //colors for table
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to Ledger APP");
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
    public static void loadTransactions() {
        String line;
        try {

            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));

            while ((line = reader.readLine()) != null) {

                String[] divider = line.split("\\|");
                LocalDate dayOfTransactions = LocalDate.parse(divider[0], DATE_FMT);
                LocalTime timeOfTransactions = LocalTime.parse(divider[1], TIME_FMT);
                String description = divider[2].trim();
                String vendor = divider[3].trim();
                double amount = Double.parseDouble(divider[4]);

                Transaction transaction = new Transaction(dayOfTransactions, timeOfTransactions, description, vendor, amount);
                transactions.add(transaction);

            }
            reader.close();
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
            LocalDateTime dateTime = null;
            while (dateTime == null) {
                System.out.print("Enter date and time of transaction (yyyy-MM-dd HH:mm:ss): ");
                String inputDateTime = scanner.nextLine().trim();
                try {
                    dateTime = LocalDateTime.parse(inputDateTime, DATETIME_FMT);
                } catch (Exception ex) {
                    System.out.println("Invalid date Or time");
                }
            }

            System.out.print("Enter description of transaction: ");
            String inputDescription = scanner.nextLine().trim();
            while (inputDescription.isEmpty()) {
                System.out.print("Please Enter a DESCRIPTION for the transaction: ");
                inputDescription = scanner.nextLine().trim();
            }

            System.out.print("Enter vendor of transaction: ");
            String inputVendor = scanner.nextLine().trim();
            while (inputVendor.isEmpty()) {
                System.out.print("Please Enter a VENDOR for the transaction: ");
                inputVendor = scanner.nextLine().trim();
            }

            System.out.print("Enter amount of transaction: ");
            double inputAmount = 0;
            while (inputAmount <= 0) {
                String userAmount = scanner.nextLine().trim();
                try {
                    inputAmount = parseDouble(userAmount);
                    if (inputAmount <= 0) {
                        System.out.print("Enter a bigger amount for the transaction: ");
                    }
                } catch (Exception ex) {
                    System.out.print("Please enter a valid amount: ");
                }
            }
            double amount = inputAmount;

            LocalDate date = dateTime.toLocalDate();
            LocalTime time = dateTime.toLocalTime();

            Transaction transaction = new Transaction(date, time, inputDescription, inputVendor, amount);
            transactions.add(transaction);

            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
            buffWriter.newLine();
            buffWriter.write(transaction.toString(date,time,inputDescription,inputVendor,amount));
            buffWriter.close();

            System.out.println("Money saved");
            System.out.println();

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
            LocalDateTime dateTime = null;
            while (dateTime == null) {
                System.out.print("Enter date and time of transaction (yyyy-MM-dd HH:mm:ss): ");
                String inputDateTime = scanner.nextLine().trim();
                try {
                    dateTime = LocalDateTime.parse(inputDateTime, DATETIME_FMT);
                } catch (Exception ex) {
                    System.out.println("Invalid date Or time");
                }
            }

            System.out.print("Enter description of transaction: ");
            String inputDescription = scanner.nextLine().trim();
            while (inputDescription.isEmpty()) {
                System.out.print("Please Enter a DESCRIPTION for the transaction: ");
                inputDescription = scanner.nextLine().trim();
            }

            System.out.print("Enter vendor of transaction: ");
            String inputVendor = scanner.nextLine().trim();
            while (inputVendor.isEmpty()) {
                System.out.print("Please Enter a VENDOR for the transaction: ");
                inputVendor = scanner.nextLine().trim();
            }

            System.out.print("Enter amount of transaction: ");
            double inputAmount = 0;
            while (inputAmount <= 0) {
                String userAmount = scanner.nextLine().trim();
                try {
                    inputAmount = parseDouble(userAmount);
                    if (inputAmount <= 0) {
                        System.out.print("Enter a bigger amount for the transaction: ");
                    }
                } catch (Exception ex) {
                    System.out.print("Please enter a valid amount: ");
                }
            }
            double amount = inputAmount;

            LocalDate date = dateTime.toLocalDate();
            LocalTime time = dateTime.toLocalTime();

            Transaction transaction = new Transaction(date, time, inputDescription, inputVendor, -amount);
            transactions.add(transaction);

            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
            buffWriter.newLine();
            buffWriter.write(transaction.toString(date,time,inputDescription,inputVendor,-amount));
            buffWriter.close();

            System.out.println("Payment Made");
            System.out.println();

        } catch (Exception ex) {
            System.err.println("something went wrong!!!");
        }
        // TODO
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        sortArray();

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
        sortArray();
        tableHeader();
        for (Transaction transaction : transactions) {
            System.out.println(ANSI_PURPLE + transaction + ANSI_RESET);
        }
        tableFooter();
        /* TODO – print all transactions in column format */
    }

    private static void displayDeposits() {
        sortArray();
        tableHeader();
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                System.out.println(ANSI_GREEN + transaction + ANSI_RESET);
            }
        }
        tableFooter();
        /* TODO – only amount > 0               */
    }

    private static void displayPayments() {
        sortArray();
        tableHeader();
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.println(ANSI_RED + transaction + ANSI_RESET);
            }
            /* TODO – only amount < 0               */
        }
        tableFooter();
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        sortArray();
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
                case "1" -> /* TODO – month-to-date report */
                    //filters transactions from today and the first of this month
                        filterTransactionsByDate(LocalDate.now().withDayOfMonth(1), LocalDate.now());
                case "2" -> {
                    //filters transactions from the first of last month and the last day of last month
                    LocalDate firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                    LocalDate lastDayLastOfMonth = LocalDate.now().withDayOfMonth(1).minusDays(1);

                    filterTransactionsByDate(firstDayOfLastMonth, lastDayLastOfMonth);
                    /* TODO – previous month report */
                }
                case "3" -> {
                    //filters transactions from the first of this year to today
                    LocalDate firstDayOfYear = LocalDate.now().withDayOfYear(1);

                    filterTransactionsByDate(firstDayOfYear, LocalDate.now());

                    /* TODO – year-to-date report   */
                }
                case "4" -> {
                    //filters transactions from the first of last year and to the last day of last year
                    LocalDate firstDayOfLastYear = LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear());
                    LocalDate lastDayOfLastYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).minusDays(1);

                    filterTransactionsByDate(firstDayOfLastYear, lastDayOfLastYear);

                    /* TODO – previous year report  */
                }
                case "5" -> {
                    //filers transactions by vendor name that the user inputs
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
        sortArray();
        tableHeader();
        for (Transaction transaction : transactions) {
            if (transaction.getDate().isBefore(end) && transaction.getDate().isAfter(start)) {
                System.out.println(ANSI_PURPLE + transaction + ANSI_RESET);
            }
        }
        tableFooter();
        // TODO – iterate transactions, print those within the range
    }

    //filters all transaction by vendor name from user
    private static void filterTransactionsByVendor(String vendor) {
        sortArray();
        tableHeader();
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                System.out.println(ANSI_BLUE + transaction + ANSI_RESET);
            }
        }
        tableFooter();
        // TODO – iterate transactions, print those with matching vendor
    }

    private static void customSearch(Scanner scanner) {
        sortArray();

        System.out.println("Enter starting Date Or skip by pressing Enter (yyyy-MM-dd): ");
        LocalDate startingDate = parseDate(scanner.nextLine().trim());

        System.out.println("Enter ending Date Or skip by pressing Enter (yyyy-MM-dd): ");
        LocalDate endingDate = parseDate(scanner.nextLine().trim());

        System.out.println("enter a description Or skip by pressing Enter: ");
        String description = scanner.nextLine().trim();

        System.out.println("Enter name of vendor Or skip by pressing Enter: ");
        String vendor = scanner.nextLine().trim();

        System.out.println("Enter amount Or skip by pressing Enter: ");
        Double amount = parseDouble(scanner.nextLine().trim());

        // goes though every transaction in the array and only prints if everything is true
        tableHeader();
        for (Transaction transaction : transactions) {
            boolean onOrOff = true;

            //is not null and is before start date
            if (startingDate != null && transaction.getDate().isBefore(startingDate)) {
                onOrOff = false;
            }
            // is not null and is after the end date
            if (endingDate != null && transaction.getDate().isAfter(endingDate)) {
                onOrOff = false;
            }
            //is not empty and does not match any description
            if (!description.isEmpty() && !transaction.getDescription().equalsIgnoreCase(description)) {
                onOrOff = false;
            }
            //is not empty and does not match any vendor
            if (!vendor.isEmpty() && !transaction.getVendor().equalsIgnoreCase(vendor)) {
                onOrOff = false;
            }

            if (amount != null && transaction.getAmount() >= amount) {
                onOrOff = false;
            }

            //only prints if onOrOff is true
            if (onOrOff) {
                System.out.println(ANSI_BLUE + transaction + ANSI_RESET);
            }
        }
        tableFooter();



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
    private static void sortArray(){
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime));
    }

    private static void tableHeader() {
        System.out.println("-----------+-----------------+-------------------------------------+---------------------------+---------------+");
        System.out.printf("\t%s|\t\t%2s\t |\t\t\t%s\t\t\t\t\t|\t\t%s\t\t\t|\t%s\t\t\t|\n",
                "Date   ", "Time","Description", "Vendor", "Amount");
        System.out.println("-----------+-----------------+-------------------------------------+---------------------------+---------------+");
    }

    private static void tableFooter() {
        System.out.println("-----------+-----------------+-------------------------------------+---------------------------+---------------+");
    }
}