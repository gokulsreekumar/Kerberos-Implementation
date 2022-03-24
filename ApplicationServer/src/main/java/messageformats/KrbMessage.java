package messageformats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(privateNoargConstructor = true)
@JsonSerialize(as = ImmutableKrbMessage.class)
@JsonDeserialize(as = ImmutableKrbMessage.class)
public interface KrbMessage {
    // Message Header
    int applicationNumber();
    // Message Body
    byte[] krbMessageBody();
}
