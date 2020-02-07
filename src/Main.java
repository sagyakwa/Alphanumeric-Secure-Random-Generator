import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) {
        TransactionGenerator tr = new TransactionGenerator();
        System.out.println(tr.generateRandomID(Paths.get("sample.csv")));
    }
}
