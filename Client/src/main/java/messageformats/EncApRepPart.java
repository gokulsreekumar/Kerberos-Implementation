package messageformats;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;

public class EncApRepPart implements Serializable {
    Timestamp ctime;
    // Integer cusec;
    private Optional<Integer> seqNumber;

    public EncApRepPart() {

    }

    public EncApRepPart(Timestamp ctime) {
        this.ctime = ctime;
    }

    public EncApRepPart(Timestamp ctime, Optional<Integer> seqNumber) {
        this.ctime = ctime;
        this.seqNumber = seqNumber;
    }

    public Timestamp getCtime() {
        return ctime;
    }

    public void setCtime(Timestamp ctime) {
        this.ctime = ctime;
    }

    public Optional<Integer> getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(Optional<Integer> seqNumber) {
        this.seqNumber = seqNumber;
    }
}
