package entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import messageformats.*;
import utils.EncryptionData;
import utils.Helpers;
import utils.PrivateKeyEncryptor;
import utils.UserAuthData;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static utils.Constants.*;
import static utils.Constants.KERBEROS_VERSION_NUMBER;
import static utils.Helpers.addDays;

public class TicketGrantingServer {
    /* The server port to which
    the client socket is going to connect */
    public final static int SERVICE_PORT = 50002;
    private static ArrayList<UserAuthData> userAuthDatabase;
    private KrbKdcReq clientRequest;
    private KrbKdcRep replyForClient;


    public static void main(String[] args) {
        TicketGrantingServer ticketGrantingServer = new TicketGrantingServer();
        while (true) {
            ticketGrantingServer.receiveClientRequestAndReply();
        }
    }

    public void receiveClientRequestAndReply() {
        try {
            /* Instantiate a new DatagramSocket to receive responses from the client */
            DatagramSocket serverSocket = new DatagramSocket(SERVICE_PORT);

            /*
             * Instantiate a UDP packet to store the client data using the buffer for receiving data
             */
            byte[] receivingDataBuffer = new byte[5120];

            System.out.println("Waiting for client request...");

            /* Receive data from the client and store in inputPacket */
            DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            serverSocket.receive(inputPacket);

            /* get the data bytes from inputPacket and convert byte array to json string */
            byte[] dataReceived = inputPacket.getData();
            String dataString = new String(dataReceived);

            /* Deserialization of json string to object */
            ObjectMapper objectMapper = new ObjectMapper();
            clientRequest = objectMapper.readValue(dataString, KrbKdcReq.class);
            System.out.println(clientRequest.toString());

            /* Obtain client's IP address and the port */
            InetAddress senderAddress = inputPacket.getAddress();
            int senderPort = inputPacket.getPort();

            // TODO: Add logic for verification of client's identity and other AS functions
//            loadUserAuthData();

//            String clientPassword = getClientCredentials(clientRequest.reqBody().getCname().getNameString());

            constructReplyForClient("jessiya@123");

            /* Serialization of reply object into json string */
            String json = objectMapper.writeValueAsString(replyForClient);

            /* Convert string to byte array */
            byte[] data = json.getBytes();

            /* Creating a UDP packet */
            DatagramPacket replyPacket = new DatagramPacket(data, data.length, senderAddress, senderPort);

            /* Send the created packet to client */
            serverSocket.send(replyPacket);

            /* Close the socket connection */
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void constructReplyForClient(String clientPassword) throws NoSuchAlgorithmException, JsonProcessingException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        /*
         Two Parts: EncKdcRepPart and Ticket
            - EncKdcRepPart uses Client's Private Key
            - Ticket uses TGS's Private Key for its Encrypted Part
        */

        Timestamp authTime = new Timestamp(System.currentTimeMillis());
        SecretKey secretKeyBetweenClientAndTgs = PrivateKeyEncryptor.generateSessionKey(256);
        EncryptionKey sessionKeyBetweenClientAndTgs = new EncryptionKey(
                1,
                secretKeyBetweenClientAndTgs.getEncoded());

        EncKdcRepPart encKdcRepPart = new EncKdcRepPart(
                sessionKeyBetweenClientAndTgs,
                TGS_SERVER,
                Helpers.generateNonce(16),
                authTime,
                addDays(authTime, 1)
        );

        EncTicketPart encTicketPart = new EncTicketPart(
                sessionKeyBetweenClientAndTgs,
                clientRequest.reqBody().getCname(),
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
        EncryptionData encryptedEncTicketPart = PrivateKeyEncryptor.getEncryptionUsingPassword(
                jsonEncTicketPart,
                TGS_PASSWORD);

        PaData[] paData = new PaData[2];
        paData[0] = new PaData(PA_PW_SALT, encryptedEncKdcRepPart.getSalt());
        paData[1] = new PaData(PA_PW_IV, encryptedEncKdcRepPart.getIv());

        Ticket ticket = new Ticket(
                TICKET_VERSION_NUMBER,
                TGS_SERVER,
                new EncryptedData(
                        1,
                        KERBEROS_VERSION_NUMBER,
                        encryptedEncTicketPart.getCipherText().getBytes(StandardCharsets.UTF_8)));

        replyForClient = ImmutableKrbKdcRep.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(AS_REPLY_MESSSAGE_TYPE)
                .paData(paData)
                .cname(clientRequest.reqBody().getCname())
                .ticket(ticket)
                .encPart(new EncryptedData(
                        1,
                        KERBEROS_VERSION_NUMBER,
                        encryptedEncKdcRepPart.getCipherText().getBytes(StandardCharsets.UTF_8)))
                .build();
        System.out.println("ciphertext"+encryptedEncKdcRepPart.getCipherText());
    }

}
