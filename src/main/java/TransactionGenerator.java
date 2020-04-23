/*
 Author: Samuel Agyakwa
 Date: 02/20/2020

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


 In this application, for every row in the cvs file, the information is put into an arraylist of stringbuilders, in the
 generateRandomIDs method. The generateRandomAlphaNumeric method then takes it in as a string and adds it to the reseeding
 of the random generation.
 */


import org.jetbrains.annotations.NotNull;
import utils.CustomCSVReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DrbgParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Transaction Generator class implementation
 */
public class TransactionGenerator {

    private int cvsLineCounter;  // To count csv lines and print to log
    private SecureRandom secureRandomObject; // Instantiation of our SecureRandom object

    /**
     * Constructor that sets our counter for the csv lines, and creates a new SecureRandom instance.
     */
    public TransactionGenerator() {
        this.cvsLineCounter = 0;
        this.secureRandomObject = new SecureRandom();
    }


    /**
     * This method takes in a CSV file path, and puts each line into the generateRandomAlphaNumeric method.
     * Using the Custom CSV class, just reading the 1.5 million samples took 0.46 seconds average
     * This includes reading the file twice (to initialize ArrayList).
     *
     * @param csvFilePath is the String path of the CSV file
     * @param withHeader is a boolean value to be set if your CSV file has a header line (the first line)
     * @return an ArrayList of random alphanumerics
     * @throws IOException in case CSV file doesn't exist, or there are any IO errors
     */
    // Read data from CSV File and generate random IDs in a list
    protected List<StringBuilder> generateAllAlphaNumericID(String csvFilePath, boolean withHeader) throws IOException {
        // Logging
        logToConsole("Creating initial capacity for array list");
        // Get size of CSV
        int initialCapacity = (int) Files.lines(Paths.get(csvFilePath)).count();
        // Set initial capacity of our list using the size of the CSV file
        List<StringBuilder> randomIDList = new ArrayList<>(initialCapacity);
        String line;

        // Logging
        logToConsole("Getting ready to read CSV file line by line ");
        logToConsole("Setting Secure Random Algorithm to DRBG");

        try {
            // Read CSV file. For each row make random id for each line
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Paths.get(csvFilePath).toString()))));
            if (withHeader) // Treat the first line as a header
                reader.readLine();

            while ((line = reader.readLine()) != null) {
                this.cvsLineCounter++;
                randomIDList.add(generateRandomAlphaNumericString(CustomCSVReader.parseCSVLine(line)));
            }

            // Logging
            logToConsole("Closing our buffered reader");
            reader.close(); // Close buffered reader
        } catch (IOException e) {
            // Logging
            logToConsole(e.toString());
            e.printStackTrace();
        }

        // Logging
        logToConsole("Returning our array list of 24 alphanumeric characters");
        return randomIDList;
    }

    /**
     * Overloaded function to set default parameter value for withHeader equal to true
     *
     * @param csvFilePath is the String path of the CSV file
     * @return an ArrayList object of all generated random alphanumeric ID numbers
     * @throws IOException in case CSV file doesn't exist, or there are any IO errors
     */
    protected List<StringBuilder> generateAllAlphaNumericID(String csvFilePath) throws IOException {
        return generateAllAlphaNumericID(csvFilePath, true);
    }

    /**
     * This method takes in a (customer) string, set the SecureRandom algorithm to DRBG, with 256 bits of security strength,
     * Prediction resistance + reseeding, which means it's unpredictable as long as the seed is unknown, while using the
     * customer info bits as a personalization string. The personalization string is combined with a secret entropy input
     * and (possibly) a nonce to produce a seed for the secure random generation.
     *
     * @param customerInfoString is the String of the customer's information, via return from the CustomCSVReader class
     * @return a StringBuilder object containing a 24 alphanumeric secure random ID
     */
    // Implementation of random alphanumeric string containing 24 characters (no special characters)
    @NotNull
    protected final StringBuilder generateRandomAlphaNumericString(String customerInfoString) {
        // Set length of random alphanumeric
        final int maxIDLength = 24;
        StringBuilder randomAlphanumericID = new StringBuilder(maxIDLength);

        // Put customer string through the DRBG generation
        try {
            new AtomicReference<>(SecureRandom.getInstance("DRBG", DrbgParameters.instantiation(256, DrbgParameters.Capability.PR_AND_RESEED, customerInfoString.getBytes(StandardCharsets.UTF_16))));
        } catch (NoSuchAlgorithmException e) {
            // Logging
            logToConsole(e.toString());
            e.printStackTrace();
        }

        // Logging
        logToConsole("Line " + this.cvsLineCounter + ": Generating 24 alphanumeric character");
        // Go through and make 24 alphanumeric string
        for (int i = 0; i < maxIDLength; i++) {
            String acceptedCharacters = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
            int randCharAt = secureRandomObject.nextInt(acceptedCharacters.length());
            char randomCharacter = acceptedCharacters.charAt(randCharAt);

            randomAlphanumericID.append(randomCharacter);
        }

        // Logging
        logToConsole("Done with generations!");
        return randomAlphanumericID;
    }

    /**
     * Basic logging to the console. Console logging instead of File Logging is fine.
     *
     * @param dataToPrint is the string you want to print out to the console
     */
    private void logToConsole(String dataToPrint) {
        System.out.println(dataToPrint);
    }
}
