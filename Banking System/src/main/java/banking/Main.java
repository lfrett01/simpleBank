package banking;

// run the bank
public class Main {

    private static Database db;
    static final String FILENAME = "bank.db";

    public static void main(String[] args) {
        db = new Database(FILENAME);
        db.createNewTable();
        new BankApp(db);
        BankApp.mainMenu();
    }
}