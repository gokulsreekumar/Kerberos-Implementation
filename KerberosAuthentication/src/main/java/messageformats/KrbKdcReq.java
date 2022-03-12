package messageformats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Optional;

@Value.Immutable
@Value.Style(privateNoargConstructor = true)
@JsonSerialize(as = ImmutableKrbKdcReq.class)
@JsonDeserialize(as = ImmutableKrbKdcReq.class)
public interface KrbKdcReq {
//    long serialVersionUID = 1L;

    int pvno();
    int msgType();
    KrbKdcReqBody reqBody();
}

