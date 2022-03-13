package utils;

public class EncryptionData {
    String cipherText;
    byte[] salt;

    public EncryptionData(String cipherText, byte[] salt) {
        this.cipherText = cipherText;
        this.salt = salt;
    }

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

}
