package entities;

import Exceptions.IncorrectAuthenticatorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import messageformats.*;
import utils.EncryptionData;
import utils.PrivateKeyEncryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

import static java.lang.System.exit;
import static utils.Constants.AP_REPLY_MESSAGE_TYPE;
import static utils.Constants.KERBEROS_VERSION_NUMBER;
import static utils.Helpers.addMinutes;

public class ApplicationServer {
    public final static int AP_SERVICE_PORT = 50002;
    private final static byte[] apSecretKey = "abcdefghijklmnopqrstuvwxyz123456".getBytes(StandardCharsets.UTF_8);
    private DatagramSocket serverSocket;
    private KrbApReq clientRequest;
    private KrbApRep replyForClient;
    private byte[] sessionKey;

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
            clientRequest = objectMapper.readValue(dataString, KrbApReq.class);
            System.out.println(clientRequest.toString());

            /* Obtain client's IP address and the port */
            InetAddress senderAddress = inputPacket.getAddress();
            int senderPort = inputPacket.getPort();

            doClientAuthentication();

            /* TODO: Check if Reply needs to be sent by checking the ApOptions Field (Make KerberosFlags) */
            constructApReplyForClient();

            /* Serialization of reply object into json string */
            String replyForClientJsonString = objectMapper.writeValueAsString(replyForClient);

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

    private void doClientAuthentication() {
        try {
            /* Decrypt SGT from client request to retrieve session key */
            EncTicketPart unencryptedTicket = getUnencryptedTicketFromClientRequest();
            sessionKey = unencryptedTicket.getKey().getKeyValue();

            /* Decrypt Authenticator from client request to perform client authentication */
            UnencryptedAuthenticator unencryptedAuthenticator = getUnencryptedAuthenticatorFromClientRequest();

            /*
            CHECKS ON AUTHENTICATOR:
            1. Is Timestamp recent? (if issued maximum 10 minutes before)
            2. Is client name in authenticator and ticket same?
             */
            Timestamp timeStampAsSessionKeyProof = unencryptedAuthenticator.getcTime();
            Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
            if (addMinutes(timeStampAsSessionKeyProof, 10).before(currentTimeStamp)) {
                throw new IncorrectAuthenticatorException("Timestamp in client authenticator is not recent");
            }
            String cNameInAuthenticator = unencryptedAuthenticator.getCname().getNameString();
            if (! unencryptedTicket.getCname().getNameString().equals(cNameInAuthenticator)) {
                throw new IncorrectAuthenticatorException("Client name is incorrect");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* TODO: */
    private void constructApReplyForClient() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        UnencryptedAuthenticator unencryptedAuthenticator = getUnencryptedAuthenticatorFromClientRequest();
        EncApRepPart encApRepPart = new EncApRepPart(
                unencryptedAuthenticator.getcTime()
        );

        String encApRepPartJsonString = objectMapper.writeValueAsString(encApRepPart);
        EncryptionData encryptionForEncApRepPartJsonString = PrivateKeyEncryptor.getEncryptionUsingSecretKey(
                encApRepPartJsonString,
                sessionKey);

        replyForClient = ImmutableKrbApRep.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(AP_REPLY_MESSAGE_TYPE)
                .encPart(new EncryptedData(
                        1,
                        KERBEROS_VERSION_NUMBER,
                        encryptionForEncApRepPartJsonString.getIv(),
                        encryptionForEncApRepPartJsonString.getCipherText().getBytes(StandardCharsets.UTF_8)
                ))
                .build();
    }

    private EncTicketPart getUnencryptedTicketFromClientRequest() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Ticket tgt = clientRequest.ticket();
        EncryptedData encryptedDataForTicket = tgt.getEncPart();
        String cipherTextForTicket = new String(encryptedDataForTicket.getCipher(), StandardCharsets.UTF_8);
        EncryptionData encryptionDataForTicket = new EncryptionData(
                cipherTextForTicket,
                null,
                encryptedDataForTicket.getIv()
        );
        String plainTextForTicket = PrivateKeyEncryptor.getDecryptionUsingSecretKey(
                encryptionDataForTicket, apSecretKey);
        EncTicketPart unencryptedTicket = objectMapper.readValue(
                plainTextForTicket, EncTicketPart.class);
        sessionKey = unencryptedTicket.getKey().getKeyValue();
        return unencryptedTicket;
    }

    private UnencryptedAuthenticator getUnencryptedAuthenticatorFromClientRequest() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        EncryptedData encryptedDataFromAuthenticator = clientRequest.authenticator();
        String cipherTextForAuthenticator = new String(
                encryptedDataFromAuthenticator.getCipher(), StandardCharsets.UTF_8);
        EncryptionData encryptionDataForAuthenticator = new EncryptionData(
                cipherTextForAuthenticator,
                null,
                encryptedDataFromAuthenticator.getIv());
        String plainTextForAuthenticator = PrivateKeyEncryptor.getDecryptionUsingSecretKey(
                encryptionDataForAuthenticator, sessionKey);

        UnencryptedAuthenticator unencryptedAuthenticator = objectMapper.readValue(
                plainTextForAuthenticator, UnencryptedAuthenticator.class);
        return unencryptedAuthenticator;
    }

    public static void main(String[] args) {
        ApplicationServer applicationServer = new ApplicationServer();
        try {
            /* Instantiate a new DatagramSocket to receive responses from the client */
            applicationServer.serverSocket = new DatagramSocket(AP_SERVICE_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
            exit(1);
        }
        while (true) {
            applicationServer.receiveClientRequestAndReply();
        }
    }

}
