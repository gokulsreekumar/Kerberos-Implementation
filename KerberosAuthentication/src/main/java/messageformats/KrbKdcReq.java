package messageformats;

import org.immutables.value.Value;

@Value.Immutable
public interface KrbKdcReq {
    static final long serialVersionUID = 1L;

    public int getPvno();
    public int getMsgType();
    public PaData getPaData();
    public messageformats.ImmutableKrbKdcReqBody getReqBody();
}

