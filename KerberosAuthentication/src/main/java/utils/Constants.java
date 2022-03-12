package utils;

import messageformats.PrincipalName;

public class Constants {
    public static final int KerberosVersionNumber = 5;
    public static final int AsRequestMesssageType = 10;
    public static final int TgsRequestMesssageType = 12;

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

    public static final PrincipalName as_server = new PrincipalName("as_server");
    public static final PrincipalName tgs_server = new PrincipalName("tgs_server");

}
