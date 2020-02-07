/*
The purpose of this program is to generate a secure 24 alphanumerical digit number. Since Math.Random and the Random() class are pseudo random generators, 
it would not be secure. The SecureRandom library is best suited for a thread safe, true random generator. On Windows, the default implementation for SecureRandom 
is SHA1PRNG on Windows, and on Linux/Solaris/Mac, the default implementation is NativePRNG. SHA1PRNG can be 17 times fater than NativePRNG, but seeding options are fixed.
Another implementation is AESCounterRNG, which is 10x faster than SHA1PRNG, and also continuously receives entropy from /dev/urandom, unlike the other PRNGs, 
but you sacrifice stability. THe DRBG implementation in Java 9+ returns a SecureRandom object of the specific algorithm supporting the specific instantiate parameters.
The implementation's effective instantiated parameters must match this minimum request but is not necessarily the same.
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.DrbgParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class TransactionGenerator {
    private final String csvFile;
    private static final String acceptedSymbols = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
    // Returns a SecureRandom object of the specific algorithm supporting the specific instantiate parameters.
    // This implementation in Java 9+ uses SeedGenerator as entropy input, which reads entropy from java.secritoty.egd
    // or /dev/random on linux/solaris/mac
    private final SecureRandom secureRandom = SecureRandom.getInstance("DRBG", DrbgParameters.instantiation(256, DrbgParameters.Capability.PR_AND_RESEED, acceptedSymbols.getBytes()));

    public TransactionGenerator(String csvFile) throws NoSuchAlgorithmException {
        this.csvFile = csvFile;
    }

    // Read data from CSV File. Static void for now may change later
    public static void readCsvLineByLine(String file) throws IOException, CsvValidationException {
        //Instantiating the CSVReader class
        CSVReader reader = new CSVReader(new FileReader(file));
        //Reading the csv file
        StringBuffer buffer = new StringBuffer();
        String line[];
        while ((line = reader.readNext()) != null) {
            for(int i = 0; i<line.length; i++) {
                System.out.print(line[i]+" ");
            }
            System.out.println(" ");
        }

        /* Output will look something like this
        id  name    rand_num    rand_date   address
        -----------------------------------------------
        1   Sam     30          2020-01-28  10 Lynn Lane
        2   Tom     40          2020-06-17  25 Worcester Rd
        .
        .
        .
        etc
        */
    }

    // Implementation of random alphanumeric string
    protected String generateID(){
        // Set byte value
        final byte[] byteValue = acceptedSymbols.getBytes(); // This is where we might use the csv file data where we get the bytes from one line and make a random with it
        this.secureRandom.nextBytes(byteValue);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(byteValue);
        //right now, I get input like so: ACo-5qrnDXHDGQtBSuYo1b_mRrWjNDxEiRRFOIndJYQDTxU2KRWoyut9CAHCoSSbNx2IVRMOX90WPp_ECic
        // next task is to get it to 24 alphanumeric characters with no special charater such as - or _ ..
    }
}
