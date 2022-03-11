package messageformats;

import org.immutables.value.Value;

@Value.Immutable
public interface EncryptedData {
    int etype();
    int kvno();
    byte[] cipher();
}
