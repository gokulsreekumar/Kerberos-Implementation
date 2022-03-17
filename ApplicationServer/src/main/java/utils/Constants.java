package utils;

import messageformats.PrincipalName;

public class Constants {
    // General Version Numbers
    public static final int KERBEROS_VERSION_NUMBER = 5;
    public static final int TICKET_VERSION_NUMBER = KERBEROS_VERSION_NUMBER;
    public static final int AUTHENTICATOR_VERSION_NUMBER = KERBEROS_VERSION_NUMBER;

    // Message Types
    public static final int AS_REQUEST_MESSSAGE_TYPE = 10;
    public static final int AS_REPLY_MESSSAGE_TYPE = 11;
    public static final int TGS_REQUEST_MESSSAGE_TYPE = 12;
    public static final int TGS_REPLY_MESSSAGE_TYPE = 13;
    public static final int AP_REQUEST_MESSAGE_TYPE = 14;
    public static final int AP_REPLY_MESSAGE_TYPE = 15;

    // Hardcoded Server PrincipalNames
    public static final PrincipalName AS_SERVER = new PrincipalName("as_server");
    public static final PrincipalName TGS_SERVER = new PrincipalName("tgs_server");

    // TODO: replace this hardcoded password for TGS
    public static final String TGS_PASSWORD = "TGS_PASSWORD";

    // PaData Types
    public static final int PA_TGS_REQ = 1;
    public static final int PA_PW_SALT = 3;
    public static final int PA_PW_IV = 25; // Made Up (fin where exactly to send later)


    // Client verification return values
    public static final int AUTH_SUCCESSFUL = 0;
    public static final int AUTH_USER_NOT_FOUND = 1;
    public static final int AUTH_WRONG_USER_PSWD = 2;

    public static enum ServerType {
        AS,
        TGS,
        AP
    }
}
