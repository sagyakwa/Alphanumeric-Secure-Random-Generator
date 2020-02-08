/*
The purpose of this program is to generate a secure 24 alphanumerical digit number. Since Math.Random() and the Random()
class are cryptographically unsecure pseudo random generators. The SecureRandom library is best
suited for a thread safe, true random generator. On Windows, the default implementation for SecureRandom is SHA1PRNG on
Windows, and on Linux/Solaris/Mac, the default implementation is NativePRNG. SHA1PRNG can be 17 times fater than NativePRNG,
but seeding options are fixed. Another implementation is AESCounterRNG, which is 10x faster than SHA1PRNG, and also
continuously receives entropy from /dev/urandom, unlike the other PRNGs, but you sacrifice stability.
The DRBG implementation in Java 9+ returns a SecureRandom object of the specific algorithm supporting the specific
instantiate parameters. The implementation's effective instantiated parameters must match this minimum request but is
not necessarily the same.
 */

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DrbgParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class TransactionGenerator {
    private static final String acceptedSymbols = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
    private final SecureRandom secureRandom = new SecureRandom();

    public TransactionGenerator() {

    }

    /*
    Using the Apache Commons CSV Library. Reading the CSV file for each row took less than 1 second in total(20k nano seconds).
    This includes reading the file twice, as we do a scan to count the number of rows of data to set initial capacity of the collected instances.
     */

    // Read data from CSV File and generate random IDs in a list
    protected List<String> generateRandomID(Path filePath) {

        List<String> list = List.of();  // Default to empty list.

        try {
            // Prepare list.
            int initialCapacity = (int) Files.lines(filePath).count();
            list = new ArrayList<>(initialCapacity);

            // Read CSV file. For each row make random id for each line
            BufferedReader reader = Files.newBufferedReader(filePath);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreEmptyLines().parse(reader);
            final List<String> value = new ArrayList<>();

//            String newLine;
//            while((newLine = reader.readLine()) != null){
//                System.out.println(newLine);
//            }


            for (CSVRecord record : records) {
                LinkedHashMap<?, ?> recordMap = (LinkedHashMap<?, ?>) record.toMap();
                Collection<?> n = recordMap.values();
                System.out.println(n.toString());
                list.add(this.generateRandomAlphaNumeric(record.toString()));
                break;  // TODO: remove
                // Alternatively, if we want a specific record for the current line, we do `record.get("FirstName")`
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Implementation of random alphanumeric string containing 24 characters (no special characters)
    private String generateRandomAlphaNumeric(String customerInfoString) {

        byte[] customerBytes = new byte[18];

        try {  // set the SecureRandom algorithm to DRBG, with 256 bits of security strength, Prediction resistance and reseeding, while using the customer info bytes as a personalization string
            SecureRandom.getInstance("DRBG", DrbgParameters.instantiation(256, DrbgParameters.Capability.PR_AND_RESEED, customerInfoString.getBytes(StandardCharsets.UTF_16)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // Generate random bits into initialized array
        this.secureRandom.nextBytes(customerBytes);
        return Base64.getUrlEncoder().encodeToString(customerBytes);

        //TODO: next task is to get it to 24 alphanumeric characters with no special character such as - or _ ..
    }
}
