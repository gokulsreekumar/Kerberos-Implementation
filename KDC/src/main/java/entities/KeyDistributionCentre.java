package entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import messageformats.*;
import messageformats.EncKdcRepPart;
import messageformats.EncTicketPart;
import messageformats.EncryptedData;
import messageformats.EncryptionKey;
import messageformats.KrbApReq;
import messageformats.KrbKdcRep;
import messageformats.KrbKdcReq;
import messageformats.PaData;
import messageformats.PrincipalName;
import messageformats.Ticket;
import messageformats.UnencryptedAuthenticator;
import utils.EncryptionData;
import utils.PrivateKeyEncryptor;
import utils.UserAuthData;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static java.lang.System.exit;
import static utils.Constants.*;
import static utils.Helpers.addDays;
import static utils.Helpers.addMinutes;

public class KeyDistributionCentre {
    /* The server port to which
    the client socket is going to connect */
    public final static int KDC_SERVICE_PORT = 50001;
    private static ArrayList<UserAuthData> userAuthDatabase;
    private KrbKdcReq clientAsRequest;
    private KrbKdcRep asReplyForClient;
    private KrbError asErrorReplyForClient;
    private KrbKdcReq clientTgsRequest;
    private KrbKdcRep tgsReplyForClient;
    private KrbError tgsErrorReplyForClient;
    private final static byte[] tgsSecretKey = "abcdefghijklmnopqrstuvwxyz123456".getBytes(StandardCharsets.UTF_8);
    private final static byte[] apSecretKey = "abcdefghijklmnopqrstuvwxyz123456".getBytes(StandardCharsets.UTF_8);
    private byte[] sessionKeyForTgs;
    private DatagramSocket serverSocket;

    public static void main(String[] args) {
        KeyDistributionCentre keyDistributionCentre = new KeyDistributionCentre();
        try {
            /* Instantiate a new DatagramSocket to receive responses from the client */
            keyDistributionCentre.serverSocket = new DatagramSocket(KDC_SERVICE_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
            exit(1);
        }
        while (true) {
            keyDistributionCentre.receiveClientRequestAndReply();
        }
    }

    public void receiveClientRequestAndReply() {
        try {

            /*
             * Instantiate a UDP packet to store the client data using the buffer for receiving data
             */
            byte[] receivingDataBuffer = new byte[10000];

            System.out.println("Waiting for client request...\n");

            /* Receive data from the client and store in inputPacket */
            DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            serverSocket.receive(inputPacket);

            /* get the data bytes from inputPacket and convert byte array to json string */
            byte[] dataReceived = inputPacket.getData();
            String dataString = new String(dataReceived, StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            /* Deserialization of json string to object (KrbMessage) */
            KrbMessage krbMessageRequest = objectMapper.readValue(dataString, KrbMessage.class);


            if (krbMessageRequest.applicationNumber() == KRB_ERROR_MESSAGE_TYPE) {
                // DO WHATEVER NECESSARY
            } else {
                /* Deserialization of json string to object (KrbKdcReq) */
                KrbKdcReq clientRequest = objectMapper.readValue(new String(krbMessageRequest.krbMessageBody(),
                        StandardCharsets.UTF_8), KrbKdcReq.class);

                /* Obtain client's IP address and the port */
                InetAddress senderAddress = inputPacket.getAddress();
                int senderPort = inputPacket.getPort();

                int applicationNumber = 0;
                String replyForClientJsonString = null;
                switch (clientRequest.msgType()) {

                    case AS_REQUEST_MESSSAGE_TYPE:
                        System.out.println("Received AS Request from client "+clientRequest.reqBody().getCname().getNameString());
                        clientAsRequest = clientRequest;
                        // TODO: Add logic for verification of client's identity and other AS functions
                        loadUserAuthData();
                        String clientPassword = getClientCredentials(clientRequest.reqBody().getCname().getNameString());
//                        System.out.println("The client password is {" + clientPassword + "}");

                        if (clientPassword.equals("USER_NOT_FOUND")) {
                            // User does not exist
                            applicationNumber = KRB_ERROR_MESSAGE_TYPE;
                            constructAsErrorMessageReplyForClient();
                            replyForClientJsonString = objectMapper.writeValueAsString(asErrorReplyForClient);
                        } else {
                            applicationNumber = AS_REPLY_MESSSAGE_TYPE;
                            constructAsReplyForClient(clientPassword);
                            /* Serialization of reply object into json string */
                            replyForClientJsonString = objectMapper.writeValueAsString(asReplyForClient);
                            System.out.println("Sent AS Reply to client: \n"+ asReplyForClient.toString());
                        }

                        break;

                    case TGS_REQUEST_MESSSAGE_TYPE:
                        System.out.println("Received TGS Request from client "+clientRequest.reqBody().getCname().getNameString());
                        clientTgsRequest = clientRequest;
                        int ret = doClientAuthenticationFromTgsRequest();

                        if (ret != SUCCESS) {
                            // User does not exist
                            applicationNumber = KRB_ERROR_MESSAGE_TYPE;
                            constructTgsErrorMessageReplyForClient(ret);
                            replyForClientJsonString = objectMapper.writeValueAsString(tgsErrorReplyForClient);
                        } else {
                            applicationNumber = TGS_REQUEST_MESSSAGE_TYPE;
                            constructTgsReplyForClient();
                            /* Serialization of reply object into json string */
                            replyForClientJsonString = objectMapper.writeValueAsString(tgsReplyForClient);
                            System.out.println("Sent TGS Reply to client: \n"+ tgsReplyForClient.toString());
                        }

                        break;
                }
                System.out.println();

                /* Convert string to byte array */
                byte[] requestData = replyForClientJsonString.getBytes(StandardCharsets.UTF_8);

                ImmutableKrbMessage krbMessageReply = ImmutableKrbMessage.builder()
                        .applicationNumber(applicationNumber)
                        .krbMessageBody(requestData)
                        .build();

                String krbMessageReplyJsonString = objectMapper.writeValueAsString(krbMessageReply);

                byte[] data = krbMessageReplyJsonString.getBytes(StandardCharsets.UTF_8);

                /* Creating a UDP packet */
                DatagramPacket replyPacket = new DatagramPacket(data, data.length, senderAddress, senderPort);

                /* Send the created packet to client */
                serverSocket.send(replyPacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
            /* Close the socket connection */
            serverSocket.close();
            exit(1);
        }
    }

    public void constructAsReplyForClient(String clientPassword) throws NoSuchAlgorithmException, JsonProcessingException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        /*
         Two Parts: EncKdcRepPart and Ticket
            - EncKdcRepPart uses Client's Private Key
            - Ticket uses TGS's Private Key for its Encrypted Part
        */

        Timestamp authTime = new Timestamp(System.currentTimeMillis());
        SecretKey secretKeyBetweenClientAndTgs = PrivateKeyEncryptor.generateSessionKey(256);
        sessionKeyForTgs = secretKeyBetweenClientAndTgs.getEncoded();
        EncryptionKey sessionKeyBetweenClientAndTgs = new EncryptionKey(
                1,
                sessionKeyForTgs
        );

        EncKdcRepPart encKdcRepPart = new EncKdcRepPart(
                sessionKeyBetweenClientAndTgs,
                TGS_SERVER,
                clientAsRequest.reqBody().getNonce(),
                authTime,
                addDays(authTime, 1)
        );

        EncTicketPart encTicketPart = new EncTicketPart(
                sessionKeyBetweenClientAndTgs,
                clientAsRequest.reqBody().getCname(),
                authTime,
                addDays(authTime, 1)
        );

        /* Serialization of encKdcRepPart into json string */
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonEncKdcRepPart = objectMapper.writeValueAsString(encKdcRepPart);
        EncryptionData encryptedEncKdcRepPart = PrivateKeyEncryptor.getEncryptionUsingPassword(
                jsonEncKdcRepPart,
                clientPassword);

        /* Serialization of encTicketPart into json string */
        String jsonEncTicketPart = objectMapper.writeValueAsString(encTicketPart);
        EncryptionData encryptedEncTicketPart = PrivateKeyEncryptor.getEncryptionUsingSecretKey(
                jsonEncTicketPart,
                tgsSecretKey);

        PaData[] paData = new PaData[2];
        paData[0] = new PaData(PA_PW_SALT, encryptedEncKdcRepPart.getSalt());
        paData[1] = new PaData(PA_PW_IV, encryptedEncKdcRepPart.getIv());

        Ticket ticket = new Ticket(
                TICKET_VERSION_NUMBER,
                TGS_SERVER,
                new EncryptedData(
                        1,
                        KERBEROS_VERSION_NUMBER,
                        encryptedEncTicketPart.getIv(),
                        encryptedEncTicketPart.getCipherText().getBytes(StandardCharsets.UTF_8)));

        asReplyForClient = ImmutableKrbKdcRep.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(AS_REPLY_MESSSAGE_TYPE)
                .paData(paData)
                .cname(clientAsRequest.reqBody().getCname())
                .ticket(ticket)
                .encPart(new EncryptedData(
                        1,
                        KERBEROS_VERSION_NUMBER,
                        encryptedEncKdcRepPart.getCipherText().getBytes(StandardCharsets.UTF_8)))
                .build();
//        System.out.println("ciphertext" + encryptedEncKdcRepPart.getCipherText());
    }

    private void constructAsErrorMessageReplyForClient() {
        asErrorReplyForClient = ImmutableKrbError.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(KRB_ERROR_MESSAGE_TYPE)
                .stime(new Timestamp(System.currentTimeMillis()))
                .errorCode(KDC_ERR_C_PRINCIPAL_UNKNOWN)
                .cname(clientAsRequest.reqBody().getCname())
                .sname(clientAsRequest.reqBody().getSname())
                .eText("Client not found in Kerberos database")
                .build();

    }

    private void constructTgsErrorMessageReplyForClient(int ret) {
        int errorCode = ret;
        String eText = null;
        if (ret == KRB_AP_ERR_SKEW) {
            eText = "Clock skew too great - Timestamp is not recent";
        } else if (ret == KRB_AP_ERR_BADMATCH) {
            eText = "Ticket and authenticator don’t match";
        }
        tgsErrorReplyForClient = ImmutableKrbError.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(KRB_ERROR_MESSAGE_TYPE)
                .stime(new Timestamp(System.currentTimeMillis()))
                .errorCode(errorCode)
                .cname(clientAsRequest.reqBody().getCname())
                .sname(clientAsRequest.reqBody().getSname())
                .eText(eText)
                .build();
    }

    public void loadUserAuthData() throws IOException, CsvValidationException {
//        File file = new File("src/main/java/resources/ClientAuthenticationDatabase.csv");
//        // Create a fileReader object
//        FileReader fileReader = new FileReader(file.getPath());
//
//        // Create a csvReader object by passing fileReader as parameter
//        CSVReader csvReader = new CSVReader(fileReader);
//
//        ArrayList<UserAuthData> allUserAuthData = new ArrayList<>();
//        // Reading client record line by line
//        String[] nextRecord;
//        while ((nextRecord = csvReader.readNext()) != null) {
//            UserAuthData oneUserAuthData = new UserAuthData();
//            for (int _i = 0; _i < 2; _i++) {
//                System.out.print(nextRecord[_i] + "\t");
//                if (_i == 0) {
//                    oneUserAuthData.setUserid(nextRecord[_i]);
//                } else if (_i == 1) {
//                    oneUserAuthData.setPassword(nextRecord[_i]);
//                }
//            }
//            allUserAuthData.add(oneUserAuthData);
//            System.out.println();
//        }
        ArrayList<UserAuthData> allUserAuthData = new ArrayList<>();
        UserAuthData jessiya = new UserAuthData("joyjes", "jessiya@123");
        UserAuthData gokul = new UserAuthData("sreekg", "gokul@123");

        allUserAuthData.add(jessiya); allUserAuthData.add(gokul);

        userAuthDatabase = allUserAuthData;
    }

    public String getClientCredentials(String clientUserid) {
//        System.out.println("Client User Id = " + clientUserid);
        for (UserAuthData user : userAuthDatabase) {
//            System.out.println("USER = " + user.getUserid());
            if (clientUserid.equals(user.getUserid())) {
//                System.out.println("Im here");
                return user.getPassword();
            }
        }
        return "USER_NOT_FOUND";
    }

    private int doClientAuthenticationFromTgsRequest() {
        try {

            String apReqJson = new String(clientTgsRequest.paData()[0].getPadataValue(), StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            KrbApReq apReqInTgsRequest = objectMapper.readValue(apReqJson, KrbApReq.class);

//            System.out.println("appp"+apReqInTgsRequest.toString());

            /*
                Decrypt TGT from AP_REQ using secret key of TGS to retrieve session key
            */
            Ticket tgt = apReqInTgsRequest.ticket();
            EncryptedData encryptedDataForTicket = tgt.getEncPart();
            String cipherTextForTicket = new String(encryptedDataForTicket.getCipher(), StandardCharsets.UTF_8);
            EncryptionData encryptionDataForTicket = new EncryptionData(
                    cipherTextForTicket,
                    null,
                    encryptedDataForTicket.getIv()
            );
            String plainTextForTicket = PrivateKeyEncryptor.getDecryptionUsingSecretKey(
                    encryptionDataForTicket, tgsSecretKey);
            EncTicketPart unencryptedTicketFromTgsReq = objectMapper.readValue(
                    plainTextForTicket, EncTicketPart.class);
            sessionKeyForTgs = unencryptedTicketFromTgsReq.getKey().getKeyValue();

            /*
                Decrypt Authenticator from AP_REQ using the obtained session key
            */
            EncryptedData encryptedDataForAuthenticator = apReqInTgsRequest.authenticator();
            String cipherTextForAuthenticator = new String(
                    encryptedDataForAuthenticator.getCipher(), StandardCharsets.UTF_8);
            EncryptionData encryptionDataForAuthenticator = new EncryptionData(
                    cipherTextForAuthenticator,
                    null,
                    encryptedDataForAuthenticator.getIv());
            String plainTextForAuthenticator = PrivateKeyEncryptor.getDecryptionUsingSecretKey(
                    encryptionDataForAuthenticator, sessionKeyForTgs);
            UnencryptedAuthenticator unencryptedAuthenticatorFromTgsReq = objectMapper.readValue(
                    plainTextForAuthenticator, UnencryptedAuthenticator.class);

            /*
            CHECKS ON AUTHENTICATOR:
            1. Is Timestamp recent? (if issued maximum 5 minutes before)
            2. Is client name in authenticator and ticket same?
             */
            Timestamp timeStampAsSessionKeyProof = unencryptedAuthenticatorFromTgsReq.getcTime();
            Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
            if (addMinutes(timeStampAsSessionKeyProof, 5).before(currentTimeStamp)) {
                return KRB_AP_ERR_SKEW;
            }
            String cNameInAuthenticator = unencryptedAuthenticatorFromTgsReq.getCname().getNameString();
            if (! unencryptedTicketFromTgsReq.getCname().getNameString().equals(cNameInAuthenticator)) {
                return KRB_AP_ERR_BADMATCH;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return SUCCESS;
    }

    public void constructTgsReplyForClient() throws NoSuchAlgorithmException, JsonProcessingException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        /*
         Mainly two Parts in Tgs Reply: ticket and encPart
            - ticket: Service Granting Ticket(SGT) for client to present to Ap Server
                (uses AP's Private Key for its Encrypted Part)
            - encPart:
        */

        PrincipalName applicationServerPrincipalName = clientTgsRequest.reqBody().getSname();

        Timestamp authTime = new Timestamp(System.currentTimeMillis());

        /* Generating Session Key for Client - AP Server Communication */
        SecretKey secretKeyBetweenClientAndApServer = PrivateKeyEncryptor.generateSessionKey(256);
        EncryptionKey sessionKeyBetweenClientAndApServer = new EncryptionKey(
                1,
                secretKeyBetweenClientAndApServer.getEncoded());

        /* Creating EncKdcRepPart object */
        EncKdcRepPart encKdcRepPart = new EncKdcRepPart(
                sessionKeyBetweenClientAndApServer,
                applicationServerPrincipalName,
                clientTgsRequest.reqBody().getNonce(),
                authTime,
                addDays(authTime, 1)
        );
        /* Serialization of encKdcRepPart into json string */
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonEncKdcRepPart = objectMapper.writeValueAsString(encKdcRepPart);
        EncryptionData encryptionDataForEncKdcRep = PrivateKeyEncryptor.getEncryptionUsingSecretKey(
                jsonEncKdcRepPart,
                sessionKeyForTgs);


        /* Creating EncTicketPart object */
        EncTicketPart encTicketPart = new EncTicketPart(
                sessionKeyBetweenClientAndApServer,
                clientTgsRequest.reqBody().getCname(),
                authTime,
                addDays(authTime, 1)
        );
        /* Serialization of encTicketPart into json string */
        String jsonEncTicketPart = objectMapper.writeValueAsString(encTicketPart);
        EncryptionData encryptionDataForTicket = PrivateKeyEncryptor.getEncryptionUsingSecretKey(
                jsonEncTicketPart,
                apSecretKey);
        /* Creating Ticket object */
        Ticket ticket = new Ticket(
                TICKET_VERSION_NUMBER,
                applicationServerPrincipalName,
                new EncryptedData(
                        1,
                        KERBEROS_VERSION_NUMBER,
                        encryptionDataForTicket.getIv(),
                        encryptionDataForTicket.getCipherText().getBytes(StandardCharsets.UTF_8)));

        PaData[] paData = new PaData[0];

        /* Creating KrbKdcRep object */
        tgsReplyForClient = ImmutableKrbKdcRep.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(AS_REPLY_MESSSAGE_TYPE)
                .paData(paData)
                .cname(clientTgsRequest.reqBody().getCname())
                .ticket(ticket)
                .encPart(new EncryptedData(
                        1,
                        KERBEROS_VERSION_NUMBER,
                        encryptionDataForEncKdcRep.getIv(),
                        encryptionDataForEncKdcRep.getCipherText().getBytes(StandardCharsets.UTF_8)))
                .build();

//        System.out.println("ciphertext"+encryptionDataForEncKdcRep.getCipherText());
    }
}
