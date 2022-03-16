package messageformats;

import java.sql.Timestamp;
import java.util.Optional;

public class UnencryptedAuthenticator {
    private int authenticatorVNo;
    private PrincipalName cname;
    //    private Optional<> cksum;
    private Timestamp cTime;
    private Optional<Integer> seqNumber;
    private Optional<AuthorizationData> authorizationData;

    public int getAuthenticatorVNo() {
        return authenticatorVNo;
    }

    public void setAuthenticatorVNo(int authenticatorVNo) {
        this.authenticatorVNo = authenticatorVNo;
    }

    public PrincipalName getCname() {
        return cname;
    }

    public void setCname(PrincipalName cname) {
        this.cname = cname;
    }

    public Timestamp getcTime() {
        return cTime;
    }

    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }

    public Optional<Integer> getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(Optional<Integer> seqNumber) {
        this.seqNumber = seqNumber;
    }

    public Optional<AuthorizationData> getAuthorizationData() {
        return authorizationData;
    }

    public UnencryptedAuthenticator(int authenticatorVNo, PrincipalName cname, Timestamp cTime) {
        this.authenticatorVNo = authenticatorVNo;
        this.cname = cname;
        this.cTime = cTime;
    }

    public UnencryptedAuthenticator(int authenticatorVNo, PrincipalName cname, Timestamp cTime, Optional<Integer> seqNumber, Optional<AuthorizationData> authorizationData) {
        this.authenticatorVNo = authenticatorVNo;
        this.cname = cname;
        this.cTime = cTime;
        this.seqNumber = seqNumber;
        this.authorizationData = authorizationData;
    }

    public void setAuthorizationData(Optional<AuthorizationData> authorizationData) {
        this.authorizationData = authorizationData;
    }

    public UnencryptedAuthenticator() {
    }
}
