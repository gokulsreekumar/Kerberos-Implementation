package messageformats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(privateNoargConstructor = true)
@JsonSerialize(as = ImmutableKrbKdcRep.class)
@JsonDeserialize(as = ImmutableKrbKdcRep.class)
public interface KrbKdcRep {
    int pvno();
    int msgType();
    PrincipalName cname();
    Ticket ticket();
    EncryptedData encPart(); // EncKdcRepPart
}
