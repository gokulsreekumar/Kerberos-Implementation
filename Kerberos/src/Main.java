import messageformats.ImmutableKrbKdcReq;
import messageformats.ImmutablePrincipalName;
import messageformats.KrbKdcReq;
import messageformats.PrincipalName;

import java.sql.Timestamp;

public class Main {
    public static void main(String[] args) {
        PrincipalName principalNameCname = new PrincipalName("C");
        PrincipalName principalNameSname = new PrincipalName("TGS");
        // Instantiate KrbKdcReq Class
        ImmutableKrbKdcReq krbKdcReq = new KrbKdcReq(principalNameCname, principalNameSname, Timestamp.valueOf("2022-09-01 09:01:15"), 12345, 0);
        System.out.println(krbKdcReq);
        System.out.println(krbKdcReq.getCname().getNameString());
    }
}
