package messageformats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(privateNoargConstructor = true)
@JsonSerialize(as = ImmutableKrbApReq.class)
@JsonDeserialize(as = ImmutableKrbApReq.class)
public interface KrbApReq {
    int pvno();
    int msgType();
    Ticket ticket();
    EncryptedData authenticator();
    // TODO: Keep ApOptions and check if to send APRep or not.
}
