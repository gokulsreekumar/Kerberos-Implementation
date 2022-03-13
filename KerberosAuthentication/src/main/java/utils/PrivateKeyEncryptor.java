package utils;// secret key : 128 bits
// plaintext : 600 bits
/*  */
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class PrivateKeyEncryptor {
    public static final int SECRET_KEY_LENGTH = 128;
    public static final int PLAINTEXT_LENGTH = 600;
    public static final int KEY_LENGTH = 256;
    public static final int BLOCK_SIZE = 16;
    public static final int SALT_LENGTH = 128;
    public static final String SYMMETRIC_KEY_ALGORITHM = "AES/CBC/PKCS5Padding";

    /*
      The secret session key should be generated from a Cryptographically Secure (Pseudo-)Random Number Generator.
    */
    public static SecretKey generateSessionKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    /*
     AES secret key can be derived from a given password
     using a password-based key derivation function like PBKDF2
     */
    public static SecretKey generateSecretKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        /* this method returns a SecretKeyFactory object that converts secret keys of the specified algorithm */
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        /* takes a password, salt, iteration count, and to-be-derived key length for generating PBEKey of variable-key-size PBE ciphers */
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, KEY_LENGTH);

        /* Generates a SecretKey object from the provided key specification (key material) and encodes the key */
        byte[] keyMaterial = secretKeyFactory.generateSecret(keySpec).getEncoded();

        /* Constructs a secret key from the given byte array */
        SecretKey secretKey = new SecretKeySpec(keyMaterial, "AES");

        return secretKey;
    }

    /*
        This method generates an IV(a pseudo-random value) with size = block size
     */
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[BLOCK_SIZE];

        /* Generates a user-specified number of random bytes */
        new SecureRandom().nextBytes(iv);

        /* Creates an IvParameterSpec object using the bytes in iv as the IV */
        return new IvParameterSpec(iv);
    }


    public static String encrypt(String algorithm, String plainText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        /* Returns a Cipher object that implements the specified algorithm/transformation */
        Cipher cipher = Cipher.getInstance(algorithm);

        /* Initializes this cipher for encryption with a key and a set of algorithm parameters. */
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        /* Encrypts the data given as input */
        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        /* Encodes the specified byte array into a String using the Base64 encoding scheme */
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        /* Returns a Cipher object that implements the specified algorithm/transformation */
        Cipher cipher = Cipher.getInstance(algorithm);

        /* Initializes this cipher for decryption with a key and a set of algorithm parameters. */
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        /* Decrypts the data given as input */
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));

        return new String(plainText);
    }

    public static EncryptionData getEncryptionUsingPassword(String plainText, String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        // salt value
        byte[] salt = new byte[SALT_LENGTH]; // Should be atleast 64 bits

        /* Constructs a secure random number generator (RNG) implementing the default random number algorithm */
        SecureRandom secRandom = new SecureRandom();

        /* Generates a user-specified number of random bytes */
        secRandom.nextBytes(salt) ;

        /* Generate Initialization Vector */
        IvParameterSpec ivParameterSpec = generateIv();
        byte[] iv = ivParameterSpec.getIV();

        SecretKey key = generateSecretKeyFromPassword(password, Arrays.toString(salt));

        return new EncryptionData(encrypt(SYMMETRIC_KEY_ALGORITHM, plainText, key, ivParameterSpec), salt, iv);
    }

    public static String getDecryptionUsingPassword(EncryptionData cipher, String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        SecretKey key = generateSecretKeyFromPassword(password, Arrays.toString(cipher.salt));

        return decrypt(SYMMETRIC_KEY_ALGORITHM, cipher.cipherText, key, new IvParameterSpec(cipher.iv));
    }

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
//        String input = "baeldung";
//        String password = "baeldung";
//        // salt value
//        byte[] salt = new byte[SALT_LENGTH] ; // Should be atleast 64 bits
//        SecureRandom secRandom = new SecureRandom() ;
//        secRandom.nextBytes(salt) ; // self-seeded randomizer for salt
//
//        String algorithm, plainText = null, cipherText = null;
//        SecretKey key = null;
//        try {
//            key = getKeyFromPassword(password, String.valueOf(salt));
//            IvParameterSpec ivParameterSpec = generateIv();
//            algorithm = "AES/CBC/PKCS5Padding";
//            cipherText = encrypt(algorithm, input, key, ivParameterSpec);
//            plainText = decrypt(algorithm, cipherText, key, ivParameterSpec);
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//        System.out.println(plainText);
//        System.out.println(key);
//        System.out.println(cipherText);


        EncryptionData encryptionData = getEncryptionUsingPassword("Jessiya is a good girl", "Password@123");
        System.out.println(encryptionData.cipherText);

        byte[] bytesOfCipher = encryptionData.cipherText.getBytes(StandardCharsets.UTF_8);

        EncryptionData encryptionData1 = new EncryptionData(new String(bytesOfCipher, StandardCharsets.UTF_8), encryptionData.salt, encryptionData.iv);
        String plaintText = getDecryptionUsingPassword(encryptionData1, "Password@123");
        System.out.println(plaintText);
    }
}

