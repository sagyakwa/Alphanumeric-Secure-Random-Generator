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
The DRBG is based on a DRBG mechanism as specified in this Recommendation and includes a source of randomness.
The DRBG mechanism uses an algorithm (i.e., a DRBG algorithm) that produces a sequence of bits from an initial value that
is determined by a seed that is determined from the output of the randomness source."


In this application, for every row in the cvs file, the information is put into an ArrayList of StringBuilder objects, in the
generateRandomIDs method. The generateRandomAlphaNumeric method then takes it in as a string and adds it to the reseeding
of the random generation.
*/


import org.jetbrains.annotations.NotNull;
import utils.CustomCSVReader;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DrbgParameters;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


/**
 * Transaction Generator class implementation
 */
public class TransactionGenerator {

    private int cvsLineCounter;  // To count csv lines and (possibly) print to log
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
     *
     * @param csvFilePath is the String path of the CSV file
     * @param withHeader  is a boolean value to be set if your CSV file has a header line (the first line)
     * @param withLogging is a boolean value to determine if we should log to our console
     * @return an ArrayList of StringBuilder objects of all generated random alphanumeric ID numbers
     * @throws IOException in case CSV file doesn't exist, or there are any IO errors
     */
    // Read data from CSV File and generate random IDs in a list
    protected List<StringBuilder> generateAllAlphaNumericID(String csvFilePath, boolean withHeader, boolean withLogging)
            throws IOException {
        // Logging
        if (withLogging) logToConsole("Creating initial capacity for array list");
        // Get size of CSV
        int arrayListCapacity = (int) Files.lines(Paths.get(csvFilePath)).count();
        // Set initial capacity of our list using the size of the CSV file
        List<StringBuilder> randomIDList = new ArrayList<>(arrayListCapacity);
        String currentCSVLine;

        // Logging
        if (withLogging) {
            logToConsole("Getting ready to read CSV file line by line ");
            logToConsole("Setting Secure Random Algorithm to DRBG");
        }

        try {
            // Read CSV file. For each row make random id for each line
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream
                    (new File(Paths.get(csvFilePath).toString()))));
            if (withHeader) // Treat the first line as a header
                bufferedReader.readLine();

            while ((currentCSVLine = bufferedReader.readLine()) != null) {
                this.cvsLineCounter++;
                randomIDList.add(generateRandomAlphaNumericString(CustomCSVReader.parseCSVLine(currentCSVLine), withLogging));
            }

            // Logging
            if (withLogging) logToConsole("Closing our buffered reader");
            bufferedReader.close(); // Close buffered reader
        } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | IllegalArgumentException e) {
            // Logging
            if (withLogging) logToConsole(e.toString());
            e.printStackTrace();
        }

        // Logging
        if (withLogging) logToConsole("Returning our array list of 24 alphanumeric characters");
        return randomIDList;
    }

    /**
     * Overloaded function to set default parameter value for withHeader equal to true and withLogging equal false.
     * In the default case where only the csvFilePath paremeter is passed, it is assumed the CSV has a header and should
     * be skipped, and it has no logging.
     *
     * @param csvFilePath is the String path of the CSV file
     * @return an ArrayList of StringBuilder objects of all generated random alphanumeric ID numbers
     * @throws IOException in case CSV file doesn't exist, or there are any IO errors
     */
    protected List<StringBuilder> generateAllAlphaNumericID(String csvFilePath) throws IOException {
        return generateAllAlphaNumericID(csvFilePath, true, false);
    }

    /**
     * This method takes in a (customer) string, set the SecureRandom algorithm to DRBG, with 256 bits of security
     * strength, Prediction resistance + reseeding, which means it's unpredictable as long as the seed is unknown,
     * while using the customer info bits as a personalization string. The personalization string is combined with a
     * secret entropy input and (possibly) a nonce to produce a seed for the secure random generation.
     *
     * @param customerInfoString is the String of the customer's information, using it as bits.
     * @param withLogging is the boolean value if we should log or simply return an ID
     * @return a StringBuilder object containing a 24 alphanumeric secure random ID
     * @throws NoSuchAlgorithmException if a SecureRandomSpi implementation for the specified algorithm is not available
     * from the specified provider.
     * @throws NoSuchProviderException  if a SecureRandomSpi implementation for the specified algorithm is not available
     * from the specified Provider object.
     * @throws IllegalArgumentException if the specified provider is null.
     */

    // Implementation of random alphanumeric string containing 24 characters (no special characters)
    @NotNull
    protected final StringBuilder generateRandomAlphaNumericString(@NotNull String customerInfoString, boolean withLogging)
            throws NoSuchAlgorithmException, NoSuchProviderException, IllegalArgumentException {
        // Set length of random alphanumeric
        final short maxIDLength = 24;
        StringBuilder randomAlphanumericID = new StringBuilder(maxIDLength);

        // Put customer string through the DRBG generation
        try {
            SecureRandom.getInstance("DRBG", DrbgParameters.instantiation(256,
                    DrbgParameters.Capability.PR_AND_RESEED, customerInfoString.getBytes(StandardCharsets.UTF_16)));
        } catch (NoSuchAlgorithmException e) {
            // Logging
            if (withLogging) logToConsole(e.toString());
            e.printStackTrace();
        }

        // Logging
        if (withLogging) logToConsole("Line " + this.cvsLineCounter + ": Generating 24 alphanumeric character");
        // Go through and make 24 alphanumeric string
        for (int i = 0; i < maxIDLength; i++) {
            String acceptedCharacters = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
            int randCharAt = secureRandomObject.nextInt(acceptedCharacters.length()); // pick one of our accepted character's index
            char randomCharacter = acceptedCharacters.charAt(randCharAt); // get the character at that index

            randomAlphanumericID.append(randomCharacter); // add the character to our string builder
        }

        // Logging
        if (withLogging) logToConsole("Done with generations!");
        return randomAlphanumericID;
    }

    /**
     * Overloaded function to set default parameter value for withLogging equal false. In this case, any calls to this
     * method with JUST the customerInfoString parameter will assume the CSV file has a header and there will be no logs
     * to the console.
     *
     * @param customerInfoString is the String of the customer's information, via return from the CustomCSVReader class
     * @return a StringBuilder object containing a 24 alphanumeric secure random ID
     */
    @NotNull
    protected final StringBuilder generateRandomAlphaNumericString(String customerInfoString) throws NoSuchAlgorithmException, NoSuchProviderException, IllegalArgumentException {
        return generateRandomAlphaNumericString(customerInfoString, false);
    }

    /**
     * Basic logging to the console. Console logging instead of File Logging is sufficient.
     *
     * @param dataToPrint is the string you want to print out to the console
     */
    private void logToConsole(String dataToPrint) {
        System.out.println(dataToPrint);
    }
}
