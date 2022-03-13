package messageformats;

import java.security.Timestamp;
import java.util.Optional;

public class EncTicketPart {
    EncryptionKey key;
    PrincipalName cname;
    Timestamp authTime;
    Optional<Timestamp> startTime;
    Timestamp endTime;
    Optional<Timestamp> renewTill;

    public EncTicketPart(EncryptionKey key, PrincipalName cname, Timestamp authTime, Optional<Timestamp> startTime, Timestamp endTime, Optional<Timestamp> renewTill) {
        this.key = key;
        this.cname = cname;
        this.authTime = authTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.renewTill = renewTill;
    }

    public EncTicketPart(EncryptionKey key, PrincipalName cname, Timestamp authTime, Timestamp endTime) {
        this.key = key;
        this.cname = cname;
        this.authTime = authTime;
        this.endTime = endTime;
    }

    public EncryptionKey getKey() {
        return key;
    }

    public void setKey(EncryptionKey key) {
        this.key = key;
    }

    public PrincipalName getCname() {
        return cname;
    }

    public void setCname(PrincipalName cname) {
        this.cname = cname;
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