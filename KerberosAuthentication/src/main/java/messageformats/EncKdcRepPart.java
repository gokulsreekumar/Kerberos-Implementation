package messageformats;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;


public class EncKdcRepPart implements Serializable {
    EncryptionKey key;
    PrincipalName sname;
    int nonce;
    Optional<Timestamp> keyExpiration;
    Timestamp authTime;
    Optional<Timestamp> startTime;
    Timestamp endTime;
    Optional<Timestamp> renewTill;

    public EncKdcRepPart(EncryptionKey key, PrincipalName sname, int nonce, Optional<Timestamp> keyExpiration, Timestamp authTime, Optional<Timestamp> startTime, Timestamp endTime, Optional<Timestamp> renewTill) {
        this.key = key;
        this.sname = sname;
        this.nonce = nonce;
        this.keyExpiration = keyExpiration;
        this.authTime = authTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.renewTill = renewTill;
    }

    public EncKdcRepPart(EncryptionKey key, PrincipalName sname, int nonce, Timestamp authTime, Timestamp endTime) {
        this.key = key;
        this.sname = sname;
        this.nonce = nonce;
        this.authTime = authTime;
        this.endTime = endTime;
    }

    public EncryptionKey getKey() {
        return key;
    }

    public void setKey(EncryptionKey key) {
        this.key = key;
    }

    public PrincipalName getSname() {
        return sname;
    }

    public void setSname(PrincipalName sname) {
        this.sname = sname;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public Optional<Timestamp> getKeyExpiration() {
        return keyExpiration;
    }

    public void setKeyExpiration(Optional<Timestamp> keyExpiration) {
        this.keyExpiration = keyExpiration;
    }

    public Timestamp getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Timestamp authTime) {
        this.authTime = authTime;
    }

    public Optional<Timestamp> getStartTime() {
        return startTime;
    }

    public void setStartTime(Optional<Timestamp> startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Optional<Timestamp> getRenewTill() {
        return renewTill;
    }

    public void setRenewTill(Optional<Timestamp> renewTill) {
        this.renewTill = renewTill;
    }
}
