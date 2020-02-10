import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;


public class TransactionGeneratorTest {
    @Test
    public void generateUsingOriginalCSV() {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            long startTime = System.nanoTime();
            System.out.println(tr.generateRandomIDs(Paths.get("customers.csv").toString()));
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            double elapsedTimeInSeconds = (double) Long.parseLong(String.valueOf(elapsedTime)) / 1_000_000_000;
            System.out.println(elapsedTime + " nanoseconds\n" + elapsedTimeInSeconds + " seconds");
        } catch (IOException e) {
            System.out.println("CSV file does not exist");
        }
    }

    @Test
    public void generateUsingSampleCSV() {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            long startTime = System.nanoTime();
            System.out.println(tr.generateRandomIDs(Paths.get("sample.csv").toString()));
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            double elapsedTimeInSeconds = (double) Long.parseLong(String.valueOf(elapsedTime)) / 1_000_000_000;
            System.out.println(elapsedTime + " nanoseconds\n" + elapsedTimeInSeconds + " seconds");
        } catch (IOException e) {
            System.out.println("CSV file does not exist");
        }
    }

    @Test
    public void generateUsing1MillionRowCSV() {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            long startTime = System.nanoTime();
            System.out.println(tr.generateRandomIDs(Paths.get("sample.csv").toString()));
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            double elapsedTimeInSeconds = (double) Long.parseLong(String.valueOf(elapsedTime)) / 1_000_000_000;
            System.out.println(elapsedTime + " nanoseconds\n" + elapsedTimeInSeconds + " seconds");
        } catch (IOException e) {
            System.out.println("CSV file does not exist");
        }
    }
}
