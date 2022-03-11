package messageformats;

import org.immutables.value.Value;

import java.sql.Timestamp;
import java.util.Optional;

@Value.Immutable
public interface KrbKdcReqBody {
    PrincipalName cname();
    PrincipalName sname();
    Optional<Timestamp> from();
    Timestamp till();
    int nonce();
    int etype();
}
