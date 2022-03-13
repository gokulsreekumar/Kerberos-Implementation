package messageformats;

import java.io.Serializable;

import static utils.Constants.*;

public class Ticket implements Serializable {
    int tktVno;
    PrincipalName sname;
    EncryptedData encPart; // EncTicketPart

    // For Deserialization
    public Ticket() {
    }

    public Ticket(int tktVno, PrincipalName sname, EncryptedData encPart) {
        this.tktVno = tktVno;
        this.sname = sname;
        this.encPart = encPart;
    }

    public Ticket(PrincipalName sname, EncryptedData encPart) {
        this.tktVno = TICKET_VERSION_NUMBER;
        this.sname = sname;
        this.encPart = encPart;
    }

    public int getTktVno() {
        return tktVno;
    }

    public void setTktVno(int tktVno) {
        this.tktVno = tktVno;
    }

    public PrincipalName getSname() {
        return sname;
    }

    public void setSname(PrincipalName sname) {
        this.sname = sname;
    }

    public EncryptedData getEncPart() {
        return encPart;
    }

    public void setEncPart(EncryptedData encPart) {
        this.encPart = encPart;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "tktVno=" + tktVno +
                ", sname=" + sname +
                ", encPart=" + encPart +
                '}';
    }
}