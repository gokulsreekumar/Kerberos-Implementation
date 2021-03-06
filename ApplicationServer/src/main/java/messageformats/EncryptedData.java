package messageformats;

import java.util.Arrays;

public class EncryptedData {
    int etype;
    int kvno;
    byte[] iv;
    byte[] cipher;

    // For Deserialization
    public EncryptedData() {
    }

    public EncryptedData(int etype, int kvno, byte[] cipher) {
        this.etype = etype;
        this.kvno = kvno;
        this.cipher = cipher;
    }

    public EncryptedData(int etype, int kvno, byte[] iv, byte[] cipher) {
        this.etype = etype;
        this.kvno = kvno;
        this.cipher = cipher;
        this.iv = iv;
    }

    public int getEtype() {
        return etype;
    }

    public void setEtype(int etype) {
        this.etype = etype;
    }

    public int getKvno() {
        return kvno;
    }

    public void setKvno(int kvno) {
        this.kvno = kvno;
    }

    public byte[] getCipher() {
        return cipher;
    }

    public void setCipher(byte[] cipher) {
        this.cipher = cipher;
    }

    @Override
    public String toString() {
        return "EncryptedData{" +
                "etype=" + etype +
                ", kvno=" + kvno +
                ", cipher=" + Arrays.toString(cipher) +
                '}';
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}
