package entities;

import Exceptions.NonceDisMatchException;
import com.fasterxml.jackson.databind.ObjectMapper;
import messageformats.*;
import messageformats.ImmutableKrbApRep;
import messageformats.ImmutableKrbApReq;
import messageformats.ImmutableKrbKdcRep;
import messageformats.ImmutableKrbKdcReq;
import utils.EncryptionData;
import utils.PrivateKeyEncryptor;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Random;
import java.util.Scanner;

import static java.lang.System.exit;
import static utils.Constants.*;
import static utils.Helpers.addDays;
import static utils.Helpers.generateNonce;

public class Client {
    /* The server port to which
    the client socket is going to connect */
    public final static int KDC_SERVICE_PORT = 50001;
    public static final PrincipalName client = new PrincipalName("client" + new Random().nextInt(100));
    private PrincipalName clientKerberosId;
    private String applicationServerKerberosId;
    private String loginPassword;
    private ImmutableKrbKdcReq requestToAs;
    private ImmutableKrbKdcRep replyFromAs;
    private ImmutableKrbKdcReq requestToTgs;
    private ImmutableKrbKdcRep replyFromTgs;
    private ImmutableKrbApReq requestToAp;
    private ImmutableKrbApRep replyFromAp;
    private Ticket ticketGrantingTicket;
    private Ticket serviceGrantingTicket;
    private byte[] sessionKeyWithTgs;
    private byte[] sessionKeyWithServiceServer;
    private int nonceROne;
    private int nonceRTwo;
//    private PaData derEncodingOfApReqForTgsRequest;

    public Client(PrincipalName clientKerberosId, String loginPassword) {
        this.clientKerberosId = clientKerberosId;
        this.loginPassword = loginPassword;
    }

    private void constructAuthenticationServerRequest() {
        nonceROne = generateNonce(32);
        KrbKdcReqBody krbKdcReqBody = new KrbKdcReqBody(clientKerberosId,
                TGS_SERVER,
                addDays(new Timestamp(System.currentTimeMillis()), 1),
                nonceROne,
                1);

        PaData[] paData = new PaData[0];
        requestToAs = ImmutableKrbKdcReq.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(AS_REQUEST_MESSSAGE_TYPE)
                .paData(paData)
                .reqBody(krbKdcReqBody)
                .build();
    }

    private void sendRequestToKdcAndReceiveResponse(int serverPort, ServerType serverType) {
        try {
            /*
             * Instantiate client socket.
             * No need to bind to a specific port
             */
            DatagramSocket clientSocket = new DatagramSocket();

            /* Get the IP address of the server */
            InetAddress IPAddress = InetAddress.getByName("localhost");

            /* Serialization of object into json string */
            String requestJsonString = null;
            ObjectMapper objectMapper = new ObjectMapper();
            switch (serverType) {
                case AS:
                    requestJsonString = objectMapper.writeValueAsString(requestToAs);
                    break;
                case TGS:
                    requestJsonString = objectMapper.writeValueAsString(requestToTgs);
                    break;
                case AP:
                    requestJsonString = objectMapper.writeValueAsString(requestToAp);
                    break;
            }

            /* Convert string to byte array */
            byte[] data = requestJsonString.getBytes();

            /* Creating a UDP packet */
            DatagramPacket sendingPacket = new DatagramPacket(data, data.length, IPAddress, serverPort);

            // sending UDP packet to the server
            clientSocket.send(sendingPacket);

            byte[] receivingDataBuffer = new byte[10000];

            // Get the server response
            DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            clientSocket.receive(receivingPacket);

            byte[] dataReceived = receivingPacket.getData();
            String dataString = new String(dataReceived);

            /* Deserialization of json string to object */
            switch (serverType) {
                case AS:
                    replyFromAs = objectMapper.readValue(dataString, ImmutableKrbKdcRep.class);
                    break;
                case TGS:
                    replyFromTgs = objectMapper.readValue(dataString, ImmutableKrbKdcRep.class);
                    break;
                case AP:
                    replyFromAp = objectMapper.readValue(dataString, ImmutableKrbApRep.class);
                    break;
            }

//            System.out.println(replyFromAs.toString());

            // Closing the socket connection with the server
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAsReply() {

        try {
            /* Decrypt encrypted part of the AS reply */
            EncryptionData encryptionData = new EncryptionData(
                    new String(replyFromAs.encPart().getCipher(), StandardCharsets.UTF_8),
                    replyFromAs.paData()[0].getPadataValue(),
                    replyFromAs.paData()[1].getPadataValue());
            System.out.println("ciphertext"+new String(replyFromAs.encPart().getCipher(), StandardCharsets.UTF_8));
            String plainText = PrivateKeyEncryptor.getDecryptionUsingPassword(encryptionData, loginPassword);
            System.out.println(plainText);

            /* Create EncKdcRepPart object from the json string obtained after decryption  */
            ObjectMapper objectMapper = new ObjectMapper();
            EncKdcRepPart encKdcRepPartInAsReply = objectMapper.readValue(plainText, EncKdcRepPart.class);

            /* Retrieve SessionKey With TGS Server from EncKdcRepPart object */
            sessionKeyWithTgs = encKdcRepPartInAsReply.getKey().getKeyValue();

            /* Retrieve Nonce sent to AS from EncKdcRepPart object */
            int nonceROneReceivedInReply = encKdcRepPartInAsReply.getNonce();

            /* If nonces sent and received don't match, exit) */
            if (nonceROneReceivedInReply != nonceROne) {
                throw new NonceDisMatchException("Nonce sent to AS and received from AS don't match! \n " +
                        "This usually happens when an imposter is trying to impersonate AS.");
            }

        } catch (NonceDisMatchException e) {
            exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void constructTicketGrantingServerRequest(String applicationServerKerberosId){
        try {

            ticketGrantingTicket = replyFromAs.ticket();

            PrincipalName applicationServerPrincipalName = new PrincipalName(applicationServerKerberosId);

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());

            UnencryptedAuthenticator authenticatorForTgsReq = new UnencryptedAuthenticator(
                    AUTHENTICATOR_VERSION_NUMBER,
                    clientKerberosId,
                    currentTime
            );

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonForUnencryptedAuthenticator = objectMapper.writeValueAsString(authenticatorForTgsReq);

            EncryptionData encryptionDataForAuthenticator = PrivateKeyEncryptor.getEncryptionUsingSecretKey(
                    jsonForUnencryptedAuthenticator, sessionKeyWithTgs);

            EncryptedData encryptedDataForAuthenticator = new EncryptedData(
                    1, 1,
                    encryptionDataForAuthenticator.getIv(),
                    encryptionDataForAuthenticator.getCipherText().getBytes(StandardCharsets.UTF_8));

            KrbApReq apReqForTgsPaData = ImmutableKrbApReq.builder()
                    .pvno(KERBEROS_VERSION_NUMBER)
                    .msgType(AP_REQUEST_MESSAGE_TYPE)
                    .ticket(ticketGrantingTicket)
                    .authenticator(encryptedDataForAuthenticator)
                    .build();

            PaData[] paData = new PaData[1];
            // TODO: Can DER Encoding be used?
            paData[0] = new PaData(PA_TGS_REQ ,
                    objectMapper.writeValueAsString(apReqForTgsPaData).getBytes(StandardCharsets.UTF_8));

            nonceRTwo = generateNonce(32);
            KrbKdcReqBody krbKdcReqBody = new KrbKdcReqBody(
                    clientKerberosId,
                    applicationServerPrincipalName,
                    addDays(currentTime, 1),
                    nonceRTwo,
                    1);

            requestToTgs = ImmutableKrbKdcReq.builder()
                    .pvno(KERBEROS_VERSION_NUMBER)
                    .msgType(TGS_REQUEST_MESSSAGE_TYPE)
                    .paData(paData)
                    .reqBody(krbKdcReqBody)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTgsReply() {
        try {
            /* Decrypt encrypted part of the TGS reply */
            EncryptionData encryptionData = new EncryptionData(
                    new String(replyFromAs.encPart().getCipher(), StandardCharsets.UTF_8),
                    null,
                    replyFromAs.encPart().getIv());
            System.out.println("ciphertext"+new String(replyFromAs.encPart().getCipher(), StandardCharsets.UTF_8));
            String plainText = PrivateKeyEncryptor.getDecryptionUsingPassword(encryptionData, loginPassword);
            System.out.println(plainText);

            /* Create EncKdcRepPart object from the json string obtained after decryption  */
            ObjectMapper objectMapper = new ObjectMapper();
            EncKdcRepPart encKdcRepPartInTgsReply = objectMapper.readValue(plainText, EncKdcRepPart.class);

            /* Retrieve SessionKey With ServiceServer from EncKdcRepPart object */
            sessionKeyWithServiceServer = encKdcRepPartInTgsReply.getKey().getKeyValue();

            /* Retrieve Nonce sent to TGS from EncKdcRepPart object */
            int nonceRTwoReceivedInReply = encKdcRepPartInTgsReply.getNonce();

            /* If nonces sent and received don't match, exit) */
            if (nonceRTwoReceivedInReply != nonceRTwo) {
                throw new NonceDisMatchException("Nonce sent to TGS and received from TGS don't match! \n " +
                        "This usually happens when an imposter is trying to impersonate TGS.");
            }

        } catch (NonceDisMatchException e) {
            exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void constructApplicationServerRequest() {
        try {

            serviceGrantingTicket = replyFromAs.ticket();

            PrincipalName applicationServerPrincipalName = new PrincipalName(applicationServerKerberosId);

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());

            UnencryptedAuthenticator authenticatorForApReq = new UnencryptedAuthenticator(
                    AUTHENTICATOR_VERSION_NUMBER,
                    clientKerberosId,
                    currentTime
            );

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonForUnencryptedAuthenticator = objectMapper.writeValueAsString(authenticatorForApReq);

            EncryptionData encryptionDataForAuthenticator = PrivateKeyEncryptor.getEncryptionUsingSecretKey(
                    jsonForUnencryptedAuthenticator, sessionKeyWithServiceServer);

            EncryptedData encryptedDataForAuthenticator = new EncryptedData(
                    1, 1,
                    encryptionDataForAuthenticator.getIv(),
                    encryptionDataForAuthenticator.getCipherText().getBytes(StandardCharsets.UTF_8));

            requestToAp = ImmutableKrbApReq.builder()
                    .pvno(KERBEROS_VERSION_NUMBER)
                    .msgType(AP_REQUEST_MESSAGE_TYPE)
                    .ticket(serviceGrantingTicket)
                    .authenticator(encryptedDataForAuthenticator)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequestToApAndReceiveResponse() {
    }

    public static void main(String[] args) {
        System.out.println("WELCOME TO KERBEROS AUTHENTICATION SYSTEM!");
        System.out.println("Please enter your kerberos id and password to get started");
        Scanner myObj = new Scanner(System.in);

        System.out.print("kerberos id: ");
        String clientKerberosId = myObj.nextLine();
        System.out.print("password: ");
        String password = myObj.nextLine();

        Client client = new Client(new PrincipalName(clientKerberosId), password);

        /* AS Exchange */
        client.constructAuthenticationServerRequest();
        client.sendRequestToKdcAndReceiveResponse(KDC_SERVICE_PORT, ServerType.AS);

        /* TODO: check if user is successfully authenticated */
        client.handleAsReply();

        System.out.println("Authentication Successful!");

        System.out.println("Please enter the kerberos id of the service you would like to access");
        System.out.print("service kerberos id: ");
        client.applicationServerKerberosId = myObj.nextLine();

        /* TGS Exchange */
        client.constructTicketGrantingServerRequest(client.applicationServerKerberosId);
        client.sendRequestToKdcAndReceiveResponse(KDC_SERVICE_PORT, ServerType.TGS);

        client.handleTgsReply();

        /* AP Exchange */
        client.constructApplicationServerRequest();
        client.sendRequestToApAndReceiveResponse();

    }
}
