import messageformats.*;
import messageformats.ImmutableKrbKdcReq;
import messageformats.ImmutableKrbKdcReqBody;
import messageformats.ImmutablePrincipalName;

import java.sql.Timestamp;

public class Main {
    public static void main(String[] args) {
        ImmutablePrincipalName jessiya = ImmutablePrincipalName.builder()
                .nameString("Jessiya")
                .build();
        ImmutablePrincipalName gokul = ImmutablePrincipalName.builder()
                .nameString("Gokul")
                .build();
        System.out.println(jessiya);
        System.out.println(gokul);

        ImmutableKrbKdcReqBody krbKdcReqBody = ImmutableKrbKdcReqBody.builder()
                .cname(jessiya)
                .sname(gokul)
                .from(Timestamp.valueOf("2022-03-11 08:00:00"))
                .till(Timestamp.valueOf("2022-03-11 23:59:59"))
                .nonce(101010)
                .etype(1)
                .build();

        ImmutableKrbKdcReqBody krbKdcReqBodyNoFrom = ImmutableKrbKdcReqBody.builder()
                .cname(jessiya)
                .sname(gokul)
                .till(Timestamp.valueOf("2022-03-11 23:59:59"))
                .nonce(202020)
                .etype(1)
                .build();

        ImmutableKrbKdcReq krbKdcReq = ImmutableKrbKdcReq.builder()
                .pvno(5)
                .msgType(10)
                .paData(new PaData())
                .reqBody(krbKdcReqBody)
                .build();
        ImmutableKrbKdcReq krbKdcReqNoFrom = ImmutableKrbKdcReq.builder()
                .pvno(5)
                .msgType(10)
                .paData(new PaData())
                .reqBody(krbKdcReqBodyNoFrom)
                .build();

        // Printing an example krbKdcReq message
        System.out.println(krbKdcReq);
        // Printing an example krbKdcReqNoFrom message -> From is optional field
        System.out.println(krbKdcReqNoFrom);
    }
}
