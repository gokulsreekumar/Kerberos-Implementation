package messageformats;

public class FileRequest {
    EncryptedData encryptedFileName;

    public FileRequest(EncryptedData encryptedFileName) {
        this.encryptedFileName = encryptedFileName;
    }

    public EncryptedData getEncryptedFileName() {
        return encryptedFileName;
    }

    public void setEncryptedFileName(EncryptedData encryptedFileName) {
        this.encryptedFileName = encryptedFileName;
    }
}
