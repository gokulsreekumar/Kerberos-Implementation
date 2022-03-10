package messageformats;

import org.immutables.value.Value;

@Value.Immutable
public interface PrincipalName {
    public String getNameString();
}
