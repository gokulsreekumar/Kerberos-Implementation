package messageformats;

import java.sql.Timestamp;
import java.util.Optional;

public interface ImmutableKrbKdcReqBody {
    public PrincipalName getCname();
    public PrincipalName getSname();
    public Optional<Timestamp> getFrom();
    public Timestamp getTill();
    public int getNonce();
    public int getEtype();
}
