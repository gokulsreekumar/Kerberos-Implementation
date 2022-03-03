package messageformats;

public interface ImmutableTicket {
    public int getTktVno();
    public PrincipalName getSname();
    public EncryptedData getEncPart();
}
