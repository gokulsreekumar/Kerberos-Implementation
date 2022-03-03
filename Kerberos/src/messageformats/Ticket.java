package messageformats;

public class Ticket  implements ImmutableTicket {
    private int tktVno;
    private PrincipalName sname;
    private EncryptedData encPart;

    public Ticket(int tktVno, PrincipalName sname, EncryptedData encPart) {
        this.tktVno = tktVno;
        this.sname = sname;
        this.encPart = encPart;
    }

    public Ticket(int tktVno, PrincipalName sname) {
        this.tktVno = tktVno;
        this.sname = sname;
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
}

class EncryptedData {
    // THIS IS A PLACEHOLDER - to be implement as class in a separate file
}