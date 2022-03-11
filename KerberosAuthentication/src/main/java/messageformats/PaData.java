package messageformats;

import org.immutables.value.Value;

@Value.Immutable
public interface PaData {
    int padataType();
    byte[] padataValue();
}
