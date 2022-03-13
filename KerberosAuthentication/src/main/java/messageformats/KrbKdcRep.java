package messageformats;

import org.immutables.value.Value;

@Value.Immutable
public interface KrbKdcRep {
    int pvno();
    int msg_type();
    /*
    TODO:
    - check padata
    - principalname
     */
    // padata
    String cname();
    Ticket ticket();
    EncKdcRepPart encPart();
}
