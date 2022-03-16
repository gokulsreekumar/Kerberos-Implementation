package utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class HashFunction {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Get Salt
        byte[] salt = getSalt(16);

        String password = "Password@123";

        // Generate hash (using PKDF2 with HMACSHA1)
        byte[] hash = generateHash(password, salt);
        System.out.println(hash);
    }

    public static byte[] generateHash(String password, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        // Generate hash (using PKDF2 with HMACSHA256)
        // Key specification is specification of hash to be generated.
        // iterationCount determines number of times to apply the HMACSHA256 hash function.
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        // Generate the hash using repeated application HMACSHA256 with PKDF2 Key spec
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return hash;
    }

    public static byte[] getSalt(Integer length) {
        // Generate Salt (Random length number of bytes)
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

}
