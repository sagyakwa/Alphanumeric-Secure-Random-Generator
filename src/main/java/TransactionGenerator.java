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

A nonce may be required in the construction of a seed during instantation in order to
provide a security cushion to block certain attacks. The nonce shall be either:
a. A value with at least (1/2 security_strength) bits of entropy,
b. A value that is expected to repeat no more often than a (1/2 security_strength)-bit
random string would be expected to repeat.
For case a, the nonce may be acquired from the same source and at the same time as the
entropy input. In this case, the seed could be considered to be constructed from an "extra
strong" entropy input and the optional personalization string, where the entropy for the
entropy input is equal to or greater than (3/2 security_strength) bits.
The nonce provides greater assurance that the DRBG provides security_strength bits of
security to the consuming application. When a DRBG is instantiated many times without a
nonce, a compromise may become more likely. In some consuming applications, a single
DRBG compromise may reveal long-term secrets (e.g., a compromise of the DSA permessage secret may reveal the signing key).

During instantiation, a personalization string should be used to derive the seed. The intent of a personalization string is to differentiate this DRBG
instantiation from all other instantiations that might ever be created. The personalization
string should be set to some bitstring that is as unique as possible, and may include secret
information. Secret information should not be used in the personalization string if it
requires a level of protection that is greater than the intended security strength of the
DRBG instantiation. Good choices for the personalization string contents include:
- Device serial numbers
- Public keys
- USER IDENTIFICATION (What we'll be using)
- Per-module or per-device values
- Timestamps
- Network addresses
- Special key values
- DRBG instantiation
- Application identifiers
- Protocol version identifiers
- Random numbers
- Nonces
- Seedfiles

Source -https://www.wired.com/images_blogs/threatlevel/2013/09/SP800-90A2.pdf

The following notes apply to the "DRBG" implementation in the SUN provider of the JDK reference implementation.
This implementation supports the Hash_DRBG and HMAC_DRBG mechanisms with DRBG algorithm SHA-224, SHA-512/224, SHA-256,
SHA-512/256, SHA-384 and SHA-512, and CTR_DRBG (both using derivation function and not using derivation function) with
DRBG algorithm AES-128, AES-192 and AES-256. The mechanism name and DRBG algorithm name are determined by the security
property securerandom.drbg.config. The default choice is Hash_DRBG with SHA-256. For each combination, the security
strength can be requested from 112 up to the highest strength it supports. Both reseeding and prediction resistance are
supported. Personalization string is supported through the DrbgParameters.Instantiation class and additional input is
supported through the DrbgParameters.NextBytes and DrbgParameters.Reseed classes. If a DRBG is not instantiated with a
DrbgParameters.Instantiation object explicitly, this implementation instantiates it with a default requested strength of
128 bits, no prediction resistance request, and no personalization string. These default instantiation parameters can
also be customized with the securerandom.drbg.config security property. This implementation reads fresh entropy from the
system default entropy source determined by the security property securerandom.source. Calling SecureRandom.generateSeed(int)
will directly read from this system default entropy source. This implementation has passed all tests included in the
20151104 version of The DRBG Test Vectors.

Source - https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/DrbgParameters.html



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
import java.security.Security;
import java.security.DrbgParameters;
import java.security.SecureRandom;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


/**
 * Transaction Generator class implementation
 */
public class TransactionGenerator {

    private int cvsLineCounter;  // To count csv lines and (possibly) print to log
    private SecureRandom secureRandomObject; // Instantiation of our SecureRandom object
    private String acceptedCharacters;

    /**
     * Constructor that sets our counter for the csv lines, and creates a new SecureRandom instance.
     */
    public TransactionGenerator() {
        this.cvsLineCounter = 0;
        this.acceptedCharacters = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
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
        final String HASH_DRBG = "HASH_DRBG";
        final String HMAC_DRBG = "HMAC_DRBG";
        final String secureRandomConfig = "securerandom.drbg.config";
        final String secureRandomAlgorithm = "DRBG";
        final String secureRandomProvider = "SUN";
        final int securityStrength = 256;
        StringBuilder randomAlphanumericID = new StringBuilder(maxIDLength);

        // Try to use these two DRBG mechanisms. If those two are not available, go with whatever default is available.
        /*
        HMAC-DRBG shuffles things around a bit more than Hash-DRBG and HMAC itself contains two
        hash invocations. Thus HMAC-DRBG is certainly slower. But as any possible weaknesses of HMAC will come from
        weaknesses of the underlying hash function, HMAC can't be weaker than the hash ... but it could be stronger
        (i.e. some weaknesses of a hash function will not transfer to the corresponding HMAC). For now, there is no
        weakness known in either of both constructions, though.
         */
        // Logging
        if (withLogging) logToConsole("Configuring Secure Random");

        try{
            Security.setProperty(secureRandomConfig, HASH_DRBG);
        } catch (IllegalArgumentException e){
            Security.setProperty(secureRandomConfig, HMAC_DRBG);
        } finally {
            // Put customer string through the DRBG generation
            try {
                this.secureRandomObject = SecureRandom.getInstance(secureRandomAlgorithm, DrbgParameters.instantiation(securityStrength,
                        DrbgParameters.Capability.PR_AND_RESEED, customerInfoString.getBytes(StandardCharsets.UTF_16)), secureRandomProvider);

            } catch (NoSuchAlgorithmException e) {
                // Logging
                if (withLogging) logToConsole(e.toString());
                e.printStackTrace();
            }
        }

        // Logging
        if (withLogging) logToConsole("Line " + this.cvsLineCounter + ": Generating 24 alphanumeric character");

        // Go through and make 24 alphanumeric string
        for (int i = 0; i < maxIDLength; i++) {
            int randCharAt = this.secureRandomObject.nextInt(this.acceptedCharacters.length()); // pick one of our accepted character's index
            char randomCharacter = this.acceptedCharacters.charAt(randCharAt); // get the character at that index

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
