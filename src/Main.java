import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        TransactionGenerator tr = new TransactionGenerator();
        System.out.println(tr.generateRandomIDs(Paths.get("customers.csv").toString()));
    }
}
