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

import org.jetbrains.annotations.NotNull;
import utils.CSVUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.DrbgParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


public class TransactionGenerator {
    private SecureRandom secureRandom = new SecureRandom();
    private final int idLength = 24;  // Length of ID
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    private StringBuilder stringBuilder = new StringBuilder(idLength);

    public TransactionGenerator() {

    }

    /*
    Using the Apache Commons CSV Library. Reading the CSV file for each row took less than 1 second in total(20k nano seconds).
     */

    // Read data from CSV File and generate random IDs in a list
    protected List<String> generateRandomIDs(String filePath) {

        List<String> inputList = new ArrayList<>() {
        };  // Default to empty list
        String line;

        try {
            // Read CSV file. For each row make random id for each line
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
            reader.readLine();  // Skip first line as it's the header

            while ((line = reader.readLine()) != null) {
                inputList.add(generateRandomAlphaNumeric(CSVUtils.parseCSVLine(line)));
                break;  // TODO: remove
                // TODO: get generated IDs in a list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputList;
    }

    /**
     * @param customerInfoString
     * @return
     */
    // Implementation of random alphanumeric string containing 24 characters (no special characters)
    @NotNull
    private String generateRandomAlphaNumeric(String customerInfoString) {
        // set the SecureRandom algorithm to DRBG, with 256 bits of security strength, Prediction resistance and reseeding,
        // while using the customer info bits as a personalization string. The personalization string is combined with a
        // secret entropy input and (possibly) a nonce to produce a seed
        System.out.println(customerInfoString);
        try {
            SecureRandom.getInstance("DRBG", DrbgParameters.instantiation(256, DrbgParameters.Capability.PR_AND_RESEED, customerInfoString.getBytes(StandardCharsets.UTF_16)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Go through and make 24 alphanumeric string
        for(int i = 0; i < idLength; i++){
            String acceptedSymbols = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
            int randCharAt = this.secureRandom.nextInt(acceptedSymbols.length());
            char randChar = acceptedSymbols.charAt(randCharAt);

            stringBuilder.append(randChar);
        }

        return stringBuilder.toString();


        /* Possible solution below: If Professor changes mind about alphanumeric and allows special characters, we
        can use the nextBytes() method although nextInt() is O(1)
         */
//        byte[] customerBytes = new byte[18];
        // Generate random bits into initialized array
//        this.secureRandom.nextBytes(customerBytes);
//        System.out.println(Arrays.toString(customerBytes));
//        return Base64.getUrlEncoder().withoutPadding().encodeToString(customerBytes);
        // returns random input like [OJDjyWPudSbUCvqDS-_nN]

    }
}
