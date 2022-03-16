package messageformats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(privateNoargConstructor = true)
@JsonSerialize(as = ImmutableKrbApRep.class)
@JsonDeserialize(as = ImmutableKrbApRep.class)
public interface KrbApRep {

}
