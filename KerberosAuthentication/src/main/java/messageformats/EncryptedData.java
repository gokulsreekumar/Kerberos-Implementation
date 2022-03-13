package messageformats;

public class EncryptedData {
    int etype;
    int kvno;
    byte[] cipher;

    // For Deserialization
    public EncryptedData() {
    }

    public EncryptedData(int etype, int kvno, byte[] cipher) {
        this.etype = etype;
        this.kvno = kvno;
        this.cipher = cipher;
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
}
