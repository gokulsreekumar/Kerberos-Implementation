package messageformats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(privateNoargConstructor = true)
@JsonSerialize(as = ImmutableKrbKdcReq.class)
@JsonDeserialize(as = ImmutableKrbKdcReq.class)
public interface KrbKdcReq {
//    long serialVersionUID = 1L;

    int pvno();
    int msgType();
    PaData[] paData();
    KrbKdcReqBody reqBody();
}

