/*
The purpose of this class is to generate a 24 alphanumeric string, utilizing a customer's information. Previously we used
the SecureRandom implementation provided by java.security, and while it was cryptographically secure, it was not TRULY
random. But then again, the debate of whether the world is deterministic or not comes into play when you talk about truly
random numbers. But for the project's sake, we'll assume the world is non-deterministic. Here, we'll use a
Quantom Random Bit Generator (QRBG121) this harvests entropy by measuring single-photon and entangled two-photon
polarization states. -> From the QRBG Service:

"We use 'Quantum Random Bit Generator' (QRBG121), which is a fast non-deterministic random bit (number) generator whose
randomness relies on intrinsic randomness of the quantum physical process of photonic emission in semiconductors and
subsequent detection by photoelectric effect. In this process photons are detected at random, one by one independently
of each other. Timing information of detected photons is used to generate random binary digits - bits. The unique feature
of this method is that it uses only one photon detector to produce both zeros and ones which results in a very small bias
and high immunity to components variation and aging. Furthermore, detection of individual photons is made by a
photomultiplier (PMT). Compared to solid state photon detectors the PMT's have drastically superior signal to noise
performance and much lower probability of appearing of afterpulses which could be a source of unwanted correlations.
".
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Properties;


public class TrulyRandomGenerator {

    private SecureRandom secureRandom;

    public static void main(String[] args) throws IOException {
        TrulyRandomGenerator tr = new TrulyRandomGenerator();
        System.out.println(tr.generate("ASDSASDASDASFASdASDASd"));
    }

    public TrulyRandomGenerator(){

    }

    private StringBuilder generate(String userString) throws IOException {
        QRBG qrbgRandomGenerator = new QRBG(get("username"), get("password"));
        byte[] userBytes = userString.getBytes(StandardCharsets.UTF_16);
        int idLength = 24;
        StringBuilder alphaNumerics = new StringBuilder(idLength);

        try{
            SecureRandom.getInstance("RJGODOY", "QRBG");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        // Go through and make 24 alphanumeric string
        for (int i = 0; i < idLength; i++) {
            this.secureRandom.setSeed(userString.getBytes());
            String acceptedSymbols = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
            int randCharAt = this.secureRandom.nextInt(acceptedSymbols.length());
            char randChar = acceptedSymbols.charAt(randCharAt);

            alphaNumerics.append(randChar);
        }

        return alphaNumerics;
    }

    private String get(String property) throws IOException{
        Properties prop = new Properties();
        String propFileName = "config.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null){
            prop.load(inputStream);
            return prop.getProperty(property);
        } else{
            throw new FileNotFoundException("Property File " + propFileName + " not found");
        }
    }
}
