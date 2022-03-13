package utils;// secret key : 128 bits
// plaintext : 600 bits

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
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

    /*
     AES secret key can be derived from a given password
     using a password-based key derivation function like PBKDF2
     */

    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String algorithm, String input, SecretKey key,
                                 IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key,
                                 IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }

    public static EncryptionData getEncryptionUsingPassword(String plainText, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // salt value
        byte[] salt = new byte[128]; // Should be atleast 64 bits
        SecureRandom secRandom = new SecureRandom() ;
        secRandom.nextBytes(salt) ; // self-seeded randomizer for salt

        SecretKey key = getKeyFromPassword(password, Arrays.toString(salt));
        IvParameterSpec ivParameterSpec = generateIv();
        String algorithm = "AES/CBC/PKCS5Padding";

        return new EncryptionData(encrypt(algorithm, plainText, key, ivParameterSpec), salt);
    }

    public static String getDecryptionUsingPassword(EncryptionData cipher, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        SecretKey key = getKeyFromPassword(password, Arrays.toString(cipher.salt));
        IvParameterSpec ivParameterSpec = generateIv();
        String algorithm = "AES/CBC/PKCS5Padding";
        return decrypt(algorithm, cipher.cipherText, key, ivParameterSpec);
    }

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
//        String input = "baeldung";
//        String password = "baeldung";
//        // salt value
//        byte[] salt = new byte[128] ; // Should be atleast 64 bits
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

        EncryptionData encryptionData = getEncryptionUsingPassword("Jessiya Joy is a cutiepie", "Password@123");
        System.out.println(encryptionData.cipherText);
        String plaintText = getDecryptionUsingPassword(encryptionData, "Password@123");
        System.out.println(plaintText);
    }
}

