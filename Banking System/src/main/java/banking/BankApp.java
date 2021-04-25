package banking;

import java.util.Scanner;

public class BankApp {
    private static Database db = null;
    static Scanner scanner = new Scanner(System.in);

    public BankApp(Database db) {
        BankApp.db = db;
    }

    static void mainMenu() {

        String[] menu = new String[] {"Create an account", "Log into account"};
        printMenu(menu);
        String choice = scanner.next();

        switch (choice) {
            case "1" -> createAccount();
            case "2" -> logIn();
            case "0" -> {
                System.out.println("Bye!");
                db.closeConnection();
                System.exit(0);
            }
            default -> {
                System.out.println("Unknown command");
                mainMenu();
            }
        }
        mainMenu();
    }

    private static void createAccount() {
        BankAccount newAccount = CreateAccount.create();
        // check that account doesn't already exist
        if (db.checkAccountExists(newAccount.getCardNumber())) {
            createAccount();
        }
        else {
            newAccount.printDetails();
            db.insert(newAccount.getCardNumber(), newAccount.getPinNumber());
        }
    }

    private static void logIn() {
        System.out.println("Enter your card number: ");
        String cardNo = scanner.next();
        System.out.println("Enter your PIN:");
        String pin = scanner.next();

        Boolean valid = db.checkAccountExists(cardNo, pin);
        BankAccount account = new BankAccount(cardNo, db.getBalance(cardNo));

        if (valid)  {
            System.out.println("You have successfully logged in!");
            loggedInMenu(account);
        } else {
            System.out.println("Wrong card number or PIN!");
            mainMenu();
        }
    }


    private static void loggedInMenu(BankAccount account) {

        String[] menu = new String[] {"Balance", "Add income", "Do transfer", "Close account", "Log out"};
        printMenu(menu);
        String choice = scanner.next();

        switch (choice) {
            case "1" -> Balance(account);
            case "2" -> addIncome(account);
            case "3" -> transfer(account);
            case "4" -> closeAccount(account);
            case "5" -> logOut();
            case "0" -> {
                System.out.println("Bye");
                db.closeConnection();
                System.exit(0);
            }
            default -> {
                System.out.println("Unknown command");
                loggedInMenu(account);
            }
        }
    }

    private static void closeAccount(BankAccount account) {
        db.delete(account.getCardNumber());
        System.out.println("The account has been closed!\n");
        mainMenu();
    }

    private static void transfer(BankAccount currentAccount) {
        System.out.println("Enter the card number of the account to transfer to: ");
        String otherCardNo = scanner.next();

        if (checkLuhn(otherCardNo)) {
            if (db.checkAccountExists(otherCardNo)){
                System.out.println("Enter how much money you want to transfer:");
                int transferAmount = scanner.nextInt();
                if (currentAccount.getBalance() >= transferAmount) {
                    db.transfer(currentAccount.getCardNumber(), otherCardNo, transferAmount);
                    currentAccount.setBalance(db.getBalance(currentAccount.getCardNumber()));
                } else {
                    System.out.println("Not enough money!\n");
                }

            }
            else {
                System.out.println("Such a card does not exist.");
            }

        }
        else {
            System.out.println("Probably you made a mistake in the card number.\nPlease try again!");
        }

        loggedInMenu(currentAccount);

    }

    private static boolean checkLuhn(String otherCardNo) {
        int total = 0;
        int length = otherCardNo.length();
        for (int i= 0; i < length - 1; i++){
            total = getTotal(otherCardNo, total, i);
        }
        return   (total + Integer.parseInt(otherCardNo.substring(length-1))) % 10 == 0;
    }

    static int getTotal(String otherCardNo, int total, int i) {
        int digit = Integer.parseInt(otherCardNo.substring(i, i + 1));
        if (i %2 != 0) {
            total += digit;
        }
        else {
            int x = digit * 2;
            if (x > 9){
                total += x - 9;
            } else {
                total += x;
            }
        }
        return total;
    }


    private static void addIncome(BankAccount account) {
        System.out.println("Enter income:");
        int income = scanner.nextInt();

        db.updateBalance(account.getCardNumber(),income);
        account.setBalance(db.getBalance(account.getCardNumber()));

        System.out.println("Income was added!");
        loggedInMenu(account);
    }

    private static void logOut() {
        System.out.println("You have successfully logged out!");
        mainMenu();
    }

    private static void Balance(BankAccount account) {
        System.out.println("Balance: " + account.getBalance());
        loggedInMenu(account);
    }

    private static void printMenu(String[] menu) {
        int i = 1;
        for (String item: menu){
            System.out.println(i + ". " + item);
            i++;
        }
        System.out.println("0. Exit");
    }
}
