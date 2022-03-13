package messageformats;

import java.io.Serializable;

/*
    The EncryptionKey type is the means by which cryptographic keys used
    for encryption are transferred.
 */
public class EncryptionKey implements Serializable {
    /*
       Specifies the encryption type of the encryption key
       that follows in the keyValue field.
     */
    int keyType;

    /* Contains the key itself, encoded as an octet string. */
    byte[] keyValue;

    public EncryptionKey(int keyType, byte[] keyValue) {
        this.keyType = keyType;
        this.keyValue = keyValue;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public byte[] getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(byte[] keyValue) {
        this.keyValue = keyValue;
    }
}
