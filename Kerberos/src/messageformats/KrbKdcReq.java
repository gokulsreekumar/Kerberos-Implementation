package messageformats;

public class KrbKdcReq {
    /*
      This field is included in each message, and specifies the protocol
      version number.  We follow protocol version 5.
     */
    private int pvno;
    /*
      This field indicates the type of a protocol message.
        10 -- AS -- | 12 -- TGS --
     */
    private int msgType;

    private PaData paData;

    private KrbKdcReqBody reqBody;

    public KrbKdcReq(int pvno, int msgType, PaData paData, KrbKdcReqBody reqBody) {
        this.pvno = pvno;
        this.msgType = msgType;
        this.paData = paData;
        this.reqBody = reqBody;
    }

    public int getPvno() {
        return pvno;
    }

    public void setPvno(int pvno) {
        this.pvno = pvno;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public PaData getPaData() {
        return paData;
    }

    public void setPaData(PaData paData) {
        this.paData = paData;
    }

    public KrbKdcReqBody getReqBody() {
        return reqBody;
    }

    public void setReqBody(KrbKdcReqBody reqBody) {
        this.reqBody = reqBody;
    }
}

class PaData {
    // THIS IS A PLACEHOLDER
}