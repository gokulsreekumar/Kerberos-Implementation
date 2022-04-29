package messageformats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.sql.Timestamp;
import java.util.Optional;

@Value.Immutable
@Value.Style(privateNoargConstructor = true)
@JsonSerialize(as = ImmutableKrbError.class)
@JsonDeserialize(as = ImmutableKrbError.class)
public interface KrbError {
    int pvno();
    int msgType();
    Timestamp stime();
    int errorCode();
    PrincipalName cname();
    PrincipalName sname();
    String eText();
}
