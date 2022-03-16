package messageformats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;

//@Value.Immutable
//@Value.Style(privateNoargConstructor = true)
//@JsonSerialize(as = ImmutableKrbKdcReqBody.class)
//@JsonDeserialize(as = ImmutableKrbKdcReqBody.class)
public class KrbKdcReqBody implements Serializable {
//    PrincipalName cname();
//    PrincipalName sname();
//    Optional<Timestamp> from();
//    Timestamp till();
//    int nonce();
//    int etype();
//    private static final long serialVersionUID = 1L;
    PrincipalName cname;
    PrincipalName sname;
    Optional<Timestamp> from;
    Timestamp till;
    int nonce;

    public PrincipalName getCname() {
        return cname;
    }

    public void setCname(PrincipalName cname) {
        this.cname = cname;
    }

    public PrincipalName getSname() {
        return sname;
    }

    public void setSname(PrincipalName sname) {
        this.sname = sname;
    }

    public Optional<Timestamp> getFrom() {
        return from;
    }

    public void setFrom(Optional<Timestamp> from) {
        this.from = from;
    }

    public Timestamp getTill() {
        return till;
    }

    public void setTill(Timestamp till) {
        this.till = till;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public int getEtype() {
        return etype;
    }

    public void setEtype(int etype) {
        this.etype = etype;
    }

    public KrbKdcReqBody(PrincipalName cname, PrincipalName sname, Timestamp till, int nonce, int etype) {
        this.cname = cname;
        this.sname = sname;
        this.from = from;
        this.till = till;
        this.nonce = nonce;
        this.etype = etype;
    }

    int etype;

    public KrbKdcReqBody() {
    }
}
