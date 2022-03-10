package messageformats;

import org.immutables.value.Value;

@Value.Immutable
public interface Ticket {
    public int getTktVno();
    public messageformats.ImmutablePrincipalName getSname();
    public EncryptedData getEncPart();
}

class EncryptedData {

}