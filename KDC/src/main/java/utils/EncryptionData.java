package utils;

public class EncryptionData {
    String cipherText;
    byte[] salt;
    byte[] iv;

    public EncryptionData(String cipherText, byte[] salt, byte[] iv) {
        this.cipherText = cipherText;
        this.salt = salt;
        this.iv = iv;
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

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}
