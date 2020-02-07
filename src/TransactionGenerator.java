/*
The purpose of this program is to generate a secure 24 alphanumerical digit number. Since Math.Random and the Random() class are pseudo random generators, 
it would not be secure. The SecureRandom library is best suited for a thread safe, true random generator. On Windows, the default implementation for SecureRandom 
is SHA1PRNG on Windows, and on Linux/Solaris/Mac, the default implementation is NativePRNG. SHA1PRNG can be 17 times fater than NativePRNG, but seeding options are fixed.
Another implementation is AESCounterRNG, which is 10x faster than SHA1PRNG, and also continuously receives entropy from /dev/urandom, unlike the other PRNGs, 
but you sacrifice stability. THe DRBG implementation in Java 9+ returns a SecureRandom object of the specific algorithm supporting the specific instantiate parameters.
The implementation's effective instantiated parameters must match this minimum request but is not necessarily the same.
 */

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DrbgParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class TransactionGenerator {
    private static final String acceptedSymbols = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
    // Returns a SecureRandom object of the specific algorithm supporting the specific instantiate parameters.
    // This implementation in Java 9+ uses SeedGenerator as entropy input, which reads entropy from java.secritty.egd
    // or /dev/random on linux/solaris/mac
    private final SecureRandom secureRandom = SecureRandom.getInstance("DRBG", DrbgParameters.instantiation(256, DrbgParameters.Capability.PR_AND_RESEED, acceptedSymbols.getBytes()));

    public TransactionGenerator(File csvFile) throws NoSuchAlgorithmException {
        File csvFile1 = csvFile.getAbsoluteFile();
    }

    /*
    Using the Apache Commons CSV Library. Reading the CSV file for each row took less than 1 second in total(20k nano seconds).
    This includes reading the file twice, as we do a scan to count the number of rows of data to set initial capacity of the collected instances.
     */

    // Read data from CSV File and generate random IDs in a list
    public List<String> generateRandomID(Path filePath) {

        // TODO: Add a check for valid file existing.

        List<String> list = List.of();  // Default to empty list.

        try {
            // Prepare list.
            int initialCapacity = (int) Files.lines(filePath).count();
            list = new ArrayList<>(initialCapacity);

            // Read CSV file. For each row make random id for each line
            BufferedReader reader = Files.newBufferedReader(filePath);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);

            for (CSVRecord record : records) {
                this.generateRandomAlphaNumeric(record.get(String.valueOf(record)));
                // Alternatively if we want a specific record for the current line, we do `record.get("FirstName")`
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Implementation of random alphanumeric string containing 24 characters (no special characters)
    private String generateRandomAlphaNumeric(String customerInfoString) {
        // Set byte value
        final byte[] byteValue = customerInfoString.getBytes(); // This is where we might use the csv file data where we get the bytes from one line and make a random with it
        this.secureRandom.nextBytes(byteValue);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(byteValue);
        //right now, I get output like so: ACo-5qrnDXHDGQtBSuYo1b_mRrWjNDxEiRRFOIndJYQDTxU2KRWoyut9CAHCoSSbNx2IVRMOX90WPp_ECic
        //TODO: next task is to get it to 24 alphanumeric characters with no special charater such as - or _ ..
    }
}
