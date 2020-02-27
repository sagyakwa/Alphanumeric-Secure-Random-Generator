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


 In this application, for every row in the cvs file, the information is put into an arraylist of stringbuilders, in the
 generateRandomIDs method. The generateRandomAlphaNumeric method then takes it in as a string and adds it to the reseeding
 of the random generation.
 */

// TODO: Add logging

import utils.CustomCSVReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DrbgParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class TransactionGenerator {

    private long counter = 1;
    private SecureRandom secureRandom = new SecureRandom();

    public TransactionGenerator() {

    }


    /**
     * This method takes in a CSV file path, and puts each line into the generateRandomAlphaNumeric method.
     * Using the Custom CSV class, just reading the 1.5 million samples took 0.46 seconds average
     * This includes reading the file twice (to initialize ArrayList).
     *
     * @param csvFilePath is the String path of the CSV file
     * @return ArrayList of random alphanumerics
     * @throws IOException in case CSV file doesn't exist, or there are any IO errors
     */
    // Read data from CSV File and generate random IDs in a list
    protected List<StringBuilder> generateRandomIDs(String csvFilePath, boolean withHeader) throws IOException {
        writeLog("Creating initial capacity for array list");
        int initialCapacity = (int) Files.lines(Paths.get(csvFilePath)).count(); // get size of csv
        List<StringBuilder> randomIDList = new ArrayList<>(initialCapacity);  // Default to empty list
        String line;

        try {
            // Read CSV file. For each row make random id for each line
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Paths.get(csvFilePath).toString()))));
            reader.readLine();  // Skip first line as it's the header

            while ((line = reader.readLine()) != null) {
                writeLog("Getting ready to read CSV file line by line ");
                randomIDList.add(generateRandomAlphaNumeric(CustomCSVReader.parseCSVLine(line)));
                this.counter++;
            }

            writeLog("Closing our buffered reader");
            reader.close(); // Close reader
        } catch (IOException | InvalidAlgorithmParameterException e) {
            writeLog(e.toString());
            e.printStackTrace();
        }


        writeLog("Returning our array list of 24 alphanumeric characters");
        return randomIDList;
    }

    /**
     * Overload function for withHeader parameter's default value (true)
     *
     * @param csvFilePath is the String path of the CSV file
     * @return ArrayList of random alphanumerics
     * @throws IOException in case CSV file doesn't exist, or there are any IO errors
     */
    protected List<StringBuilder> generateRandomIDs(String csvFilePath) throws IOException {
        return generateRandomIDs(csvFilePath, true);
    }

    /**
     * This method takes in a (customer) string, set the SecureRandom algorithm to DRBG, with 256 bits of security strength,
     * Prediction resistance + reseeding, which means it's unpredictable as long as the seed is unknown, while using the
     * customer info bits as a personalization string. The personalization string is combined with a secret entropy input
     * and (possibly) a nonce to produce a seed for the secure random generation
     *
     * @param customerInfoString the String of the customer's information
     * @return String of secure random alphanumeric
     */
    // Implementation of random alphanumeric string containing 24 characters (no special characters)
    protected StringBuilder generateRandomAlphaNumeric(String customerInfoString) throws InvalidAlgorithmParameterException{
        // Set length of random alphanumeric
        int idLength = 24;
        StringBuilder stringBuilder = new StringBuilder(idLength);

        // Put customer string through the DRBG generation
        writeLog("Setting DRBG algorithm");
        try {
            SecureRandom.getInstance("DRBG", DrbgParameters.instantiation(256, DrbgParameters.Capability.PR_AND_RESEED, customerInfoString.getBytes(StandardCharsets.UTF_16)));
        } catch (NoSuchAlgorithmException e) {
            writeLog(e.toString());
            e.printStackTrace();
        }

        // Go through and make 24 alphanumeric string
        writeLog("Line " + this.counter + ": Generating 24 alphanumeric character");
        for (int i = 0; i < idLength; i++) {
            String acceptedSymbols = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
            int randCharAt = this.secureRandom.nextInt(acceptedSymbols.length());
            char randChar = acceptedSymbols.charAt(randCharAt);

            stringBuilder.append(randChar);
        }


        return stringBuilder;
    }

    /**
     * Uses internal buffer to reduce real IO operations and saves time. Prints to console as well
     *
     * @param data the data you want written
     */
    // TODO: java.io.IOException: Couldn't get lock for src/main/resources/Log.log due to concurent writing to file maybe
    private static void writeLog(String data) {
        Logger logger = Logger.getLogger("Generator Logs");
        FileHandler fileHandler;

        try {
            // Configure the logger with handler and formatter
            fileHandler = new FileHandler("src/main/resources/Log.log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

            // Log messages
            logger.info(data);

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

    }
}
