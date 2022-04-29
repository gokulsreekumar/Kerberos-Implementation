package messageformats;

public class FileReply {
    EncryptedData fileEncryptedData;

    public FileReply() {
    }

    public FileReply(EncryptedData fileEncryptedData) {
        this.fileEncryptedData = fileEncryptedData;
    }

    public EncryptedData getFileEncryptedData() {
        return fileEncryptedData;
    }

    public void setFileEncryptedData(EncryptedData fileEncryptedData) {
        this.fileEncryptedData = fileEncryptedData;
    }
}
