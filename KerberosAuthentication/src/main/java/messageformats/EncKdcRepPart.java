package messageformats;

import org.immutables.value.Value;

import java.security.Timestamp;
import java.util.Optional;

@Value.Immutable
public interface EncKdcRepPart {
    // fields from EncryptedData part to prevent extending (will find a better solution later)
    int etype();
    int kvno();
    byte[] cipher();

    // key EncryptionKey,
    // last-req LastReq
    int nonce();
    Optional<Timestamp> keyExpiration();
    // flags
    Timestamp authTime();
    Optional<Timestamp> startTime();
    Timestamp endTime();
    Optional<Timestamp> renewTill();
    PrincipalName sname();
    // caddr
}
