package entities;

import Exceptions.IncorrectAuthenticatorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
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
import java.io.File;
import java.io.FileReader;
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
    private KrbKdcReq clientTgsRequest;
    private KrbKdcRep tgsReplyForClient;
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

            System.out.println("Waiting for client request...");

            /* Receive data from the client and store in inputPacket */
            DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            serverSocket.receive(inputPacket);

            /* get the data bytes from inputPacket and convert byte array to json string */
            byte[] dataReceived = inputPacket.getData();
            String dataString = new String(dataReceived);

            /* Deserialization of json string to object */
            ObjectMapper objectMapper = new ObjectMapper();
            KrbKdcReq clientRequest = objectMapper.readValue(dataString, KrbKdcReq.class);
            System.out.println(clientRequest.toString());

            /* Obtain client's IP address and the port */
            InetAddress senderAddress = inputPacket.getAddress();
            int senderPort = inputPacket.getPort();

            String replyForClientJsonString = null;
            switch (clientRequest.msgType()) {

                case AS_REQUEST_MESSSAGE_TYPE:
                    clientAsRequest = clientRequest;
                    // TODO: Add logic for verification of client's identity and other AS functions
                    loadUserAuthData();
                    // String clientPassword = getClientCredentials(clientRequest.reqBody().getCname().getNameString());
                    constructAsReplyForClient("jessiya@123");
                    /* Serialization of reply object into json string */
                    replyForClientJsonString = objectMapper.writeValueAsString(asReplyForClient);
                    break;

                case TGS_REQUEST_MESSSAGE_TYPE:
                    clientTgsRequest = clientRequest;
                    doTgsVerification();
                    constructTgsReplyForClient();
                    /* Serialization of reply object into json string */
                    replyForClientJsonString = objectMapper.writeValueAsString(tgsReplyForClient);
                    break;
            }

            /* Convert string to byte array */
            byte[] data = replyForClientJsonString.getBytes();

            /* Creating a UDP packet */
            DatagramPacket replyPacket = new DatagramPacket(data, data.length, senderAddress, senderPort);

            /* Send the created packet to client */
            serverSocket.send(replyPacket);

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
        System.out.println("ciphertext" + encryptedEncKdcRepPart.getCipherText());
    }

    public void loadUserAuthData() throws IOException, CsvValidationException {
        File file = new File("src/main/java/resources/ClientAuthenticationDatabase.csv");
        // Create a fileReader object
        FileReader fileReader = new FileReader(file.getPath());

        // Create a csvReader object by passing fileReader as parameter
        CSVReader csvReader = new CSVReader(fileReader);

        ArrayList<UserAuthData> allUserAuthData = new ArrayList<>();
        // Reading client record line by line
        String[] nextRecord;
        while ((nextRecord = csvReader.readNext()) != null) {
            UserAuthData oneUserAuthData = new UserAuthData();
            for (int _i = 0; _i < 2; _i++) {
                System.out.print(nextRecord[_i] + "\t");
                if (_i == 0) {
                    oneUserAuthData.setUserid(nextRecord[_i]);
                } else if (_i == 1) {
                    oneUserAuthData.setPassword(nextRecord[_i]);
                }
            }
            allUserAuthData.add(oneUserAuthData);
            System.out.println();
        }
        userAuthDatabase = allUserAuthData;
    }

    public String getClientCredentials(String clientUserid) {
        for (UserAuthData user : userAuthDatabase) {
            if (user.getUserid() == clientUserid) {
                return user.getPassword();
            }
        }
        return "USER_NOT_FOUND";
    }

    private void doTgsVerification() {
        try {
            KrbApReq apReqInTgsRequest = null;

            // TODO: Add logic for verification of client's identity and other TGS functions
//            for (PaData paData: clientRequest.paData()){
//                if (paData.getPadataType() == PA_TGS_REQ) {
            String aPReqJson = new String(clientTgsRequest.paData()[0].getPadataValue(), StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            apReqInTgsRequest = objectMapper.readValue(aPReqJson, KrbApReq.class);
//                }
//            }

            System.out.println("appp"+apReqInTgsRequest.toString());

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
            1. Is Timestamp recent? (if issued maximum 10 minutes before)
            2. Is client name in authenticator and tgs req body same?
             */
            Timestamp timeStampAsSessionKeyProof = unencryptedAuthenticatorFromTgsReq.getcTime();
            Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
            if (addMinutes(timeStampAsSessionKeyProof, 10).before(currentTimeStamp)) {
                throw new IncorrectAuthenticatorException("Timestamp in client authenticator is not recent");
            }
            String cNameInAuthenticator = unencryptedAuthenticatorFromTgsReq.getCname().getNameString();
            if (!clientTgsRequest.reqBody().getCname().getNameString().equals(cNameInAuthenticator)) {
                throw new IncorrectAuthenticatorException("Client name is incorrect");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void constructTgsReplyForClient() throws NoSuchAlgorithmException, JsonProcessingException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        /*
         Two Parts: EncKdcRepPart and Ticket
            - EncKdcRepPart uses Client-TGS Session Key
            - Ticket uses AP's Private Key for its Encrypted Part
        */

        PrincipalName applicationServerPrincipalName = clientTgsRequest.reqBody().getSname();

        Timestamp authTime = new Timestamp(System.currentTimeMillis());

        SecretKey secretKeyBetweenClientAndApServer = PrivateKeyEncryptor.generateSessionKey(256);
        EncryptionKey sessionKeyBetweenClientAndApServer = new EncryptionKey(
                1,
                secretKeyBetweenClientAndApServer.getEncoded());

        EncKdcRepPart encKdcRepPart = new EncKdcRepPart(
                sessionKeyBetweenClientAndApServer,
                applicationServerPrincipalName,
                clientTgsRequest.reqBody().getNonce(),
                authTime,
                addDays(authTime, 1)
        );

        EncTicketPart encTicketPart = new EncTicketPart(
                sessionKeyBetweenClientAndApServer,
                clientTgsRequest.reqBody().getCname(),
                authTime,
                addDays(authTime, 1)
        );

        /* Serialization of encKdcRepPart into json string */
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonEncKdcRepPart = objectMapper.writeValueAsString(encKdcRepPart);
        EncryptionData encryptionDataForEncKdcRep = PrivateKeyEncryptor.getEncryptionUsingSecretKey(
                jsonEncKdcRepPart,
                sessionKeyForTgs);

        /* Serialization of encTicketPart into json string */
        String jsonEncTicketPart = objectMapper.writeValueAsString(encTicketPart);
        EncryptionData encryptionDataForTicket = PrivateKeyEncryptor.getEncryptionUsingSecretKey(
                jsonEncTicketPart,
                apSecretKey);

        Ticket ticket = new Ticket(
                TICKET_VERSION_NUMBER,
                applicationServerPrincipalName,
                new EncryptedData(
                        1,
                        KERBEROS_VERSION_NUMBER,
                        encryptionDataForTicket.getCipherText().getBytes(StandardCharsets.UTF_8)));

        PaData[] paData = new PaData[0];

        tgsReplyForClient = ImmutableKrbKdcRep.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(AS_REPLY_MESSSAGE_TYPE)
                .paData(paData)
                .cname(clientTgsRequest.reqBody().getCname())
                .ticket(ticket)
                .encPart(new EncryptedData(
                        1,
                        KERBEROS_VERSION_NUMBER,
                        encryptionDataForTicket.getIv(),
                        encryptionDataForEncKdcRep.getCipherText().getBytes(StandardCharsets.UTF_8)))
                .build();

        System.out.println("ciphertext"+encryptionDataForEncKdcRep.getCipherText());
    }
}
