package messageformats;

import org.immutables.value.Value;

@Value.Immutable
public interface KrbKdcReq {
    long serialVersionUID = 1L;

    int pvno();
    int msgType();
    PaData paData();
    KrbKdcReqBody reqBody();
}

