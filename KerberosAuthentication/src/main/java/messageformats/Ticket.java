package messageformats;

import org.immutables.value.Value;

@Value.Immutable
public interface Ticket {
    int tktVno();
    PrincipalName sname();
    EncryptedData encPart();
}