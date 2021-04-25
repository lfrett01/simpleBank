package banking;

import java.util.Random;

public class CreateAccount {
    static BankAccount create() {
        String accountNum = createNewAccountNumber();
        String cardNumber = createCardNumber(accountNum);
        String pin = createPin();
        return new BankAccount(cardNumber, pin);
    }

    private static String createPin() {
        String pin = "";
        for (int i = 0; i < 4; i++){
            pin += Integer.toString(getRandomNum());
        }
        return pin;
    }

    private static int getRandomNum() {
        Random random = new Random();
        return random.nextInt(10);
    }

    private static String createCardNumber(String accountNum) {
        String bin = "400000";
        String checksum = getCheckSum(bin + accountNum);
        return bin + accountNum + checksum;
    }

    private static String getCheckSum(String accountNum) {
        int total = 0;
        for (int i= 0; i <accountNum.length(); i++){
            total = BankApp.getTotal(accountNum, total, i);
        }
        int cs = (total % 10);
        if (cs != 0) {
            cs = 10 - cs;
        }

        return  Integer.toString(cs) ;
    }

    private static String createNewAccountNumber() {
        String accountNum = "";
        for (int i = 0; i < 9; i++){
            accountNum += Integer.toString(getRandomNum());
        }
        return accountNum;

    }



}
