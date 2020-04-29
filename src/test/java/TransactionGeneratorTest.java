import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class TransactionGeneratorTest {
    @Test
    public void generateUsingOriginalCSV() {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            long startTime = System.nanoTime();
            System.out.println(tr.generateAllAlphaNumericID("src/main/java/customers.csv"));
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            double elapsedTimeInSeconds = (double) Long.parseLong(String.valueOf(elapsedTime)) / 1_000_000_000;
            System.out.println(elapsedTime + " nanoseconds\n" + elapsedTimeInSeconds + " seconds");
        } catch (IOException e) {
            System.out.println("CSV file does not exist");
        }
    }

    @Test
    public void generateUsingOriginalCSVWithoutHeader() {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            long startTime = System.nanoTime();
            System.out.println(tr.generateAllAlphaNumericID("src/main/java/customers.csv", false, true));
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            double elapsedTimeInSeconds = (double) Long.parseLong(String.valueOf(elapsedTime)) / 1_000_000_000;
            System.out.println(elapsedTime + " nanoseconds\n" + elapsedTimeInSeconds + " seconds");
        } catch (IOException e) {
            System.out.println("CSV file does not exist");
        }
    }

    @Test
    public void generateUsing1000LineCSV() {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            long startTime = System.nanoTime();
            System.out.println(tr.generateAllAlphaNumericID("src/main/java/1000_line_csv.csv"));
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            double elapsedTimeInSeconds = (double) Long.parseLong(String.valueOf(elapsedTime)) / 1_000_000_000;
            System.out.println(elapsedTime + " nanoseconds\n" + elapsedTimeInSeconds + " seconds");
        } catch (IOException e) {
            System.out.println("CSV file does not exist");
        }
    }

    @Test
    public void testDRBGImplementation() {
        TransactionGenerator tr = new TransactionGenerator();
        try {
            tr.generateRandomAlphaNumericString("This is a test string");
        } catch (NoSuchProviderException | NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        assertThat(tr.secureRandomObject.getAlgorithm(), is("DRBG"));
        assertThat(Security.getProperty("securerandom.drbg.config") , is("HASH_DRBG"));
    }

    @Test
    public void generationTime(){
        TransactionGenerator tr = new TransactionGenerator();
        try{
            long startTime = System.nanoTime();
            StringBuilder randomString = tr.generateRandomAlphaNumericString("Samuel Agyakwa 16 Myrick Avenue Worcester MA blah Blah");
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            double elapsedTimeInSeconds = (double) Long.parseLong(String.valueOf(elapsedTime)) / 1_000_000_000;
            System.out.println("Generated in: " + elapsedTime + " nanoseconds\n" + elapsedTimeInSeconds + " seconds");
            System.out.println("Random string: " + randomString);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
}
