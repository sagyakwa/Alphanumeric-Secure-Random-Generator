import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        TransactionGenerator tr = new TransactionGenerator("Test");
        System.out.print(tr.generateID());
    }
}
