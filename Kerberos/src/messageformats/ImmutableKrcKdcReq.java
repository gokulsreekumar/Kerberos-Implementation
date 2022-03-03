package messageformats;

public interface ImmutableKrcKdcReq {
    public int getPvno();
    public int getMsgType();
    public PaData getPaData();
    public KrbKdcReqBody getReqBody();
}
