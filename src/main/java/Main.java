import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args){
        TransactionGenerator tr = new TransactionGenerator();
        try {
            System.out.println(tr.generateRandomIDs(Paths.get("src/main/java/customers.csv").toString()));
        } catch (IOException e){
            System.out.println("CSV file does not exist");
        }
    }
}
