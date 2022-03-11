package messageformats;

import org.immutables.value.Value;

import java.security.Timestamp;

@Value.Immutable
public interface EncKDCRepPart {
    // key [0] EncryptionKey,
    // last-req LastReq
    int nonce();
    Timestamp keyExpiration();
    //flags
    Timestamp authTime();
    Timestamp startTime();
    Timestamp endTime();
    Timestamp renewTill();
    PrincipalName sname();
    // caddr
}
