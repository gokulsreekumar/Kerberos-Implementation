package messageformats;

import org.immutables.value.Value;

@Value.Immutable
public interface PrincipalName {
    String nameString();
}
