package banking;


public class BankAccount {
    private final String pin;
    private int balance;
    private final String cardNumber;

    public BankAccount(String cardNumber, String pin) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = 0;
    }

    public BankAccount(String cardNumber, int balance){
        this.cardNumber = cardNumber;
        this.balance = balance;
        this.pin = "";
    }

    public int getBalance() {
        return balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPinNumber() {
        return pin;
    }

    public void printDetails() {
        System.out.println("Your card number:");
        System.out.println(cardNumber);
        System.out.println("Your card PIN:");
        System.out.println(pin + "\n");
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }
}
