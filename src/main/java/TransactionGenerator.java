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


public class TransactionGenerator {

    private int cvsLineCounter;
    private SecureRandom secureRandomObject;

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
     * @return an ArrayList of random alphanumerics
     * @throws IOException in case CSV file doesn't exist, or there are any IO errors
     */
    // Read data from CSV File and generate random IDs in a list
    protected List<StringBuilder> generateRandomIDs(String csvFilePath, boolean withHeader) throws IOException {
        writeToLog("Creating initial capacity for array list");
        // Get size of csv
        int initialCapacity = (int) Files.lines(Paths.get(csvFilePath)).count();
        // Set initial capacity of our list using the size of the csv file
        List<StringBuilder> randomIDList = new ArrayList<>(initialCapacity);
        String line;

        writeToLog("Getting ready to read CSV file line by line ");
        writeToLog("Setting Secure Random Algorithm to DRBG");

        try {
            // Read CSV file. For each row make random id for each line
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Paths.get(csvFilePath).toString()))));
            if(withHeader) // Treat the first line as a header
                reader.readLine();

            while ((line = reader.readLine()) != null) {
                this.cvsLineCounter++;
                randomIDList.add(generateRandomAlphaNumeric(CustomCSVReader.parseCSVLine(line)));
            }

            writeToLog("Closing our buffered reader");
            reader.close(); // Close buffered reader
        } catch (IOException e) {
            writeToLog(e.toString());
            e.printStackTrace();
        }


        writeToLog("Returning our array list of 24 alphanumeric characters");
        return randomIDList;
    }

    /**
     * Overload function to set default parameter value for withHeader equal to true
     *
     * @param csvFilePath is the String path of the CSV file
     * @return an ArrayList object of all generated random alphanumeric ID numbers
     * @throws IOException in case CSV file doesn't exist, or there are any IO errors
     */
    protected List<StringBuilder> generateRandomIDs(String csvFilePath) throws IOException {
        return generateRandomIDs(csvFilePath, true);
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
    protected final StringBuilder generateRandomAlphaNumeric(String customerInfoString){
        // Set length of random alphanumeric
        final int maxIDLength = 24;
        StringBuilder randomAlphanumericID = new StringBuilder(maxIDLength);

        // Put customer string through the DRBG generation
        try {
            new AtomicReference<>(SecureRandom.getInstance("DRBG", DrbgParameters.instantiation(256, DrbgParameters.Capability.PR_AND_RESEED, customerInfoString.getBytes(StandardCharsets.UTF_16))));
        } catch (NoSuchAlgorithmException e) {
            writeToLog(e.toString());
            e.printStackTrace();
        }

        // Go through and make 24 alphanumeric string
        writeToLog("Line " + this.cvsLineCounter + ": Generating 24 alphanumeric character");
        for (int i = 0; i < maxIDLength; i++) {
            String acceptedSymbols = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
            int randCharAt = secureRandomObject.nextInt(acceptedSymbols.length());
            char randChar = acceptedSymbols.charAt(randCharAt);

            randomAlphanumericID.append(randChar);
        }


        writeToLog("Done with generation!");
        return randomAlphanumericID;
    }

    /**
     * Basic logging to the console. Console logging instead of File Logging is fine.
     *
     * @param data is the data you want to print out to the console
     */
    private void writeToLog(String data) {
        System.out.println(data);
    }
}
