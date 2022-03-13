package utils;

import messageformats.PrincipalName;

public class Constants {
    public static final int KERBEROS_VERSION_NUMBER = 5;
    public static final int TICKET_VERSION_NUMBER = KERBEROS_VERSION_NUMBER;
    public static final int AS_REQUEST_MESSSAGE_TYPE = 10;
    public static final int AS_REPLY_MESSSAGE_TYPE = 11;
    public static final int TGS_REQUEST_MESSSAGE_TYPE = 12;
    public static final int TGS_REPLY_MESSSAGE_TYPE = 13;

//    public static final ImmutablePrincipalName client = ImmutablePrincipalName.builder()
//            .nameString("client")
//            .build();

//    public static final ImmutablePrincipalName as_server = ImmutablePrincipalName.builder()
//            .nameString("as_server")
//            .build();

//    public static final ImmutablePrincipalName tgs_server = ImmutablePrincipalName.builder()
//            .nameString("tgs_server")
//            .build();
//
//    public static final ImmutablePrincipalName application_server = ImmutablePrincipalName.builder()
//            .nameString("application_server")
//            .build();

    public static final PrincipalName AS_SERVER = new PrincipalName("as_server");
    public static final PrincipalName TGS_SERVER = new PrincipalName("tgs_server");

}
