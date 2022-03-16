import messageformats.*;

import java.sql.Timestamp;

public class Main {
    public static void main(String[] args) {
//        ImmutablePrincipalName jessiya = ImmutablePrincipalName.builder()
//                .nameString("Jessiya")
//                .build();
//        ImmutablePrincipalName gokul = ImmutablePrincipalName.builder()
//                .nameString("Gokul")
//                .build();
//        System.out.println(jessiya);
//        System.out.println(gokul);

//        PaData paData = ImmutablePaData.builder()
//                .padataType(1)
//                .padataValue(new byte[5])
//                .build();

//        KrbKdcReqBody krbKdcReqBody = ImmutableKrbKdcReqBody.builder()
//                .cname(jessiya)
//                .sname(gokul)
//                .from(Timestamp.valueOf("2022-03-11 08:00:00"))
//                .till(Timestamp.valueOf("2022-03-11 23:59:59"))
//                .nonce(101010)
//                .etype(1)
//                .build();
//
//        KrbKdcReqBody krbKdcReqBodyNoFrom = ImmutableKrbKdcReqBody.builder()
//                .cname(jessiya)
//                .sname(gokul)
//                .till(Timestamp.valueOf("2022-03-11 23:59:59"))
//                .nonce(202020)
//                .etype(1)
//                .build();

//        KrbKdcReq krbKdcReq = ImmutableKrbKdcReq.builder()
//                .pvno(5)
//                .msgType(10)
////                .paData(paData)
//                .reqBody(krbKdcReqBody)
//                .build();
//        KrbKdcReq krbKdcReqNoFrom = ImmutableKrbKdcReq.builder()
//                .pvno(5)
//                .msgType(10)
////                .paData(paData)
//                .reqBody(krbKdcReqBodyNoFrom)
//                .build();

        // Printing an example krbKdcReq message
//        System.out.println(krbKdcReq);
//        // Printing an example krbKdcReqNoFrom message -> From is optional field
//        System.out.println(krbKdcReqNoFrom);
    }
}
