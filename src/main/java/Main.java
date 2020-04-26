/*
 Author: Samuel Agyakwa
 Date: 02/20/2020

 Main class
 */
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Main {
    public static void main(String[] args) {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            System.out.println(tr.generateAllAlphaNumericID("src/main/java/customers.csv", false, true));

        } catch (IOException e) {
            System.out.println("\n\nGenerating with one String");
            System.out.println("CSV file does not exist");
        }

        try {
            System.out.println(tr.generateRandomAlphaNumericString("\n \t â, ê, î, ô, û type a"));
            System.out.println(tr.generateRandomAlphaNumericString("`132wuawdf8o4r=-[];'.f \\/**+`"));
        } catch (NoSuchAlgorithmException | NoSuchProviderException | IllegalArgumentException e) {
            System.out.println("Invalid Algorithm has been set");
        }
    }
}
