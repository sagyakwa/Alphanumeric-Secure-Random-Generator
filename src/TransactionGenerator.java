/*
The purpose of this program is to generate a secure 24 alphanumerical digit number. Since Math.Random() and the Random()
implementations use a linear congruential generator (LCG), it is not cryptographically strong. The SecureRandom implementation
from java.security.SecureRandom uses a cryptographically strong pseudo-random number generator (CSPRNG)
suited for a thread safe, true random generator. On Windows, the default implementation for SecureRandom is SHA1PRNG on
Windows, and on Linux/Solaris/Mac, the default implementation is NativePRNG. SHA1PRNG can be 17 times fater than NativePRNG,
but seeding options are fixed. Another implementation is AESCounterRNG, which is 10x faster than SHA1PRNG, and also
continuously receives entropy from /dev/urandom, unlike the other PRNGs, but you sacrifice stability.
The DRBG implementation in Java 9+ returns a SecureRandom object of the specific algorithm supporting the specific
instantiate parameters. The implementation's effective instantiated parameters must match this minimum request but is
not necessarily the same.
 */

import utils.CSVUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DrbgParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


public class TransactionGenerator {
    private SecureRandom secureRandom = new SecureRandom();

    public TransactionGenerator() {

    }

    /*
    Using the Apache Commons CSV Library; Reading the CSV file for each row took less than 1 second in total
    (20k nano seconds avg). This includes reading the file twice to initialize ArrayList.
     */

    // Read data from CSV File and generate random IDs in a list
    protected List<String> generateRandomIDs(String csvFilePath) throws IOException {
        int initialCapacity = (int) Files.lines(Paths.get(csvFilePath)).count(); // get size of csv
        List<String> idArray = new ArrayList<>(initialCapacity);  // Default to empty list
        String line;

        try {
            // Read CSV file. For each row make random id for each line
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(csvFilePath))));
            reader.readLine();  // Skip first line as it's the header

            while ((line = reader.readLine()) != null) {

                idArray.add(generateRandomAlphaNumeric(CSVUtil.parseCSVLine(line)));
//                System.out.println(idArray);
            }

            reader.close(); // Close reader
        } catch (IOException e) {
            e.printStackTrace();
        }


        return idArray;
    }

    /**
     * @param customerInfoString the String of the customer's information
     * @return String of secure random alphanumeric
     */
    // Implementation of random alphanumeric string containing 24 characters (no special characters)
    private String generateRandomAlphaNumeric(String customerInfoString) {
        // Length of ID
        int idLength = 24;
        StringBuilder stringBuilder = new StringBuilder(idLength);

        // set the SecureRandom algorithm to DRBG, with 256 bits of security strength, Prediction resistance + reseeding,
        // while using the customer info bits as a personalization string. The personalization string is combined with a
        // secret entropy input and (possibly) a nonce to produce a seed for the secure random generation
        try {
            SecureRandom.getInstance("DRBG", DrbgParameters.instantiation(256, DrbgParameters.Capability.PR_AND_RESEED, customerInfoString.getBytes(StandardCharsets.UTF_16)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Go through and make 24 alphanumeric string
        for (int i = 0; i < idLength; i++) {
            String acceptedSymbols = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
            int randCharAt = this.secureRandom.nextInt(acceptedSymbols.length());
            char randChar = acceptedSymbols.charAt(randCharAt);

            stringBuilder.append(randChar);
        }

        return stringBuilder.toString();
    }
}
