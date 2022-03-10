package messageformats;

public interface KrcKdcReq {
    public int getPvno();
    public int getMsgType();
    public PaData getPaData();
    public KrbKdcReqBody getReqBody();
}

class PaData {

}