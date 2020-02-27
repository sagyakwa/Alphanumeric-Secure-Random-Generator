import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;


public class TransactionGeneratorTest {
    @Test
    public void generateUsingOriginalCSV() {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            long startTime = System.nanoTime();
            System.out.println(tr.generateRandomIDs(("src/main/java/customers.csv")));
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
            System.out.println(tr.generateRandomIDs(("src/main/java/36_635 row sample.csv")));
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            double elapsedTimeInSeconds = (double) Long.parseLong(String.valueOf(elapsedTime)) / 1_000_000_000;
            System.out.println(elapsedTime + " nanoseconds\n" + elapsedTimeInSeconds + " seconds");
        } catch (IOException e) {
            System.out.println("CSV file does not exist");
        }
    }

    @Test
    public void generateUsingFiveHundredThousandRows() {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            long startTime = System.nanoTime();
            System.out.println(tr.generateRandomIDs(("src/main/java/500_000 Sales Records.csv")));
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            double elapsedTimeInSeconds = (double) Long.parseLong(String.valueOf(elapsedTime)) / 1_000_000_000;
            System.out.println(elapsedTime + " nanoseconds\n" + elapsedTimeInSeconds + " seconds");
        } catch (IOException e) {
            System.out.println("CSV file does not exist");
        }
    }

    @Test
    public void generateUsingTextFile() {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            tr.generateRandomIDs(("src/main/java/Sample.txt"));
        } catch(IOException e){
            System.out.println("Doesn't exist");
        }
    }
}
