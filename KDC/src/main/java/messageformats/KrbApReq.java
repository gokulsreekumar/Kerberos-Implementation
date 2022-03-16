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
    /* KRB_AP_REQ has type as 14 */
    int msgType();
    Ticket ticket();
    EncryptedData authenticator();
}
