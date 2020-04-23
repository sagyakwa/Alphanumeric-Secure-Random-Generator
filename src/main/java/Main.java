/*
 Author: Samuel Agyakwa
 Date: 02/20/2020

 Main class
 */
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            System.out.println(tr.generateAllAlphaNumericID("src/main/java/customers.csv"));
        } catch (IOException e){
            System.out.println("CSV file does not exist");
        }
    }
}
