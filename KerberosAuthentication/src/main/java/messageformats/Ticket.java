package messageformats;

public interface Ticket {
    public int getTktVno();
    public PrincipalName getSname();
    public EncryptedData getEncPart();
}

class EncryptedData {

}