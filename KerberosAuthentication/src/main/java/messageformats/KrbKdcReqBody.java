package messageformats;

import org.immutables.value.Value;

import java.sql.Timestamp;
import java.util.Optional;

@Value.Immutable
public interface KrbKdcReqBody {
    public messageformats.ImmutablePrincipalName getCname();
    public messageformats.ImmutablePrincipalName getSname();
    public Optional<Timestamp> getFrom();
    public Timestamp getTill();
    public int getNonce();
    public int getEtype();
}
