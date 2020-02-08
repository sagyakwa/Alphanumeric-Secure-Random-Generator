import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        TransactionGenerator tr = new TransactionGenerator();
        System.out.println(tr.generateRandomID(Paths.get("customers.csv")));
    }
}
