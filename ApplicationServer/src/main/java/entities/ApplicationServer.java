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
import static utils.Constants.*;
import static utils.Constants.KRB_ERROR_MESSAGE_TYPE;
import static utils.Helpers.addMinutes;

public class ApplicationServer {
    public final static int AP_SERVICE_PORT = 50002;
    private final static byte[] apSecretKey = "abcdefghijklmnopqrstuvwxyz123456".getBytes(StandardCharsets.UTF_8);
    private DatagramSocket serverSocket;
    private KrbApReq clientRequest;
    private KrbApRep replyForClient;
    private KrbError errorReplyForClient;
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

            ObjectMapper objectMapper = new ObjectMapper();

            /* Deserialization of json string to object (KrbMessage) */
            KrbMessage krbMessageRequest = objectMapper.readValue(dataString, KrbMessage.class);

            if (krbMessageRequest.applicationNumber() == KRB_ERROR_MESSAGE_TYPE) {
                // DO WHATEVER NECESSARY
            } else {
                /* Deserialization of json string to object */
                clientRequest = objectMapper.readValue(krbMessageRequest.krbMessageBody(), KrbApReq.class);
                System.out.println(clientRequest.toString());

                /* Obtain client's IP address and the port */
                InetAddress senderAddress = inputPacket.getAddress();
                int senderPort = inputPacket.getPort();

                int ret = doClientAuthentication();

                int applicationNumber = 0;
                String replyForClientJsonString = null;
                if (ret != SUCCESS) {
                    // User does not exist
                    applicationNumber = KRB_ERROR_MESSAGE_TYPE;
                    constructApErrorMessageReplyForClient(ret);
                    replyForClientJsonString = objectMapper.writeValueAsString(errorReplyForClient);
                } else {
                    /* TODO: Check if Reply needs to be sent by checking the ApOptions Field (Make KerberosFlags) */
                    applicationNumber = AP_REPLY_MESSAGE_TYPE;
                    constructApReplyForClient();

                    /* Serialization of reply object into json string */
                    replyForClientJsonString = objectMapper.writeValueAsString(replyForClient);
                }

                /* Convert string to byte array */
                byte[] requestData = replyForClientJsonString.getBytes();

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
    private void constructApErrorMessageReplyForClient(int ret) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, JsonProcessingException {
        /* Decrypt Authenticator from client request to perform client authentication */
        UnencryptedAuthenticator unencryptedAuthenticator = getUnencryptedAuthenticatorFromClientRequest();

        int errorCode = ret;
        String eText = null;
        if (ret == KRB_AP_ERR_SKEW) {
            eText = "Clock skew too great - Timestamp is not recent";
        } else if (ret == KRB_AP_ERR_BADMATCH) {
            eText = "Ticket and authenticator don’t match";
        }
        errorReplyForClient = ImmutableKrbError.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(KRB_ERROR_MESSAGE_TYPE)
                .stime(new Timestamp(System.currentTimeMillis()))
                .errorCode(errorCode)
                .cname(unencryptedAuthenticator.getCname())
                .sname(new PrincipalName("SERVICE"))
                .eText(eText)
                .build();
    }
    private int doClientAuthentication() {
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
                return KRB_AP_ERR_SKEW;
            }
            String cNameInAuthenticator = unencryptedAuthenticator.getCname().getNameString();
            if (! unencryptedTicket.getCname().getNameString().equals(cNameInAuthenticator)) {
                return KRB_AP_ERR_BADMATCH;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS;
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
