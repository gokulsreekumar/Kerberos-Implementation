package MessageFormats;

import java.security.Timestamp;
import java.util.Optional;

public interface ImmutableKrbKdcReq {
    public PrincipalName getCname();
    public PrincipalName getSname();
    public Optional<Timestamp> getFrom();
    public Timestamp getTill();
    public int getNonce();
    public int getEtype();
}
