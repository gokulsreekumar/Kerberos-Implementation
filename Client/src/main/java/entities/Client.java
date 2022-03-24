package entities;

import Exceptions.NonceDisMatchException;
import Exceptions.TimestampMismatchException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import messageformats.*;
import messageformats.ImmutableKrbApRep;
import messageformats.ImmutableKrbApReq;
import messageformats.ImmutableKrbKdcRep;
import messageformats.ImmutableKrbKdcReq;
import utils.EncryptionData;
import utils.PrivateKeyEncryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
    public final static int AP_SERVICE_PORT = 50002;
    public static final PrincipalName client = new PrincipalName("client" + new Random().nextInt(100));
    private PrincipalName clientKerberosId;
    private String applicationServerKerberosId;
    private String loginPassword;
    private ImmutableKrbKdcReq requestToAs;
    private ImmutableKrbKdcRep replyFromAs;
    private ImmutableKrbError errorReplyFromAs;
    private ImmutableKrbKdcReq requestToTgs;
    private ImmutableKrbKdcRep replyFromTgs;
    private ImmutableKrbError errorReplyFromTgs;
    private ImmutableKrbApReq requestToAp;
    private ImmutableKrbApRep replyFromAp;
    private ImmutableKrbError errorReplyFromAp;
    private Ticket ticketGrantingTicket;
    private Ticket serviceGrantingTicket;
    private byte[] sessionKeyWithTgs;
    private byte[] sessionKeyWithServiceServer;
    private int nonceROne;
    private int nonceRTwo;

    private Timestamp authenticationServerRequestTime;
    private Timestamp ticketGrantingServerRequestTime;
    private Timestamp applicationServerRequestTime;

//    private PaData derEncodingOfApReqForTgsRequest;

    public Client(PrincipalName clientKerberosId, String loginPassword) {
        this.clientKerberosId = clientKerberosId;
        this.loginPassword = loginPassword;
    }

    /*
    This is used for request/response exchange with a server.
    Server can either be KDC or Application Server
    */
    private int sendRequestToServerAndReceiveResponse(int serverPort, ServerType serverType) {
        try {
            /*
             * Instantiate client socket.
             * No need to bind to a specific port
             */
            DatagramSocket clientSocket = new DatagramSocket();

            /* Get the IP address of the server */
            InetAddress IPAddress = InetAddress.getByName("localhost");

            /* Serialization of object into json string */
            int applicationNumber = 0;
            String requestJsonString = null;
            ObjectMapper objectMapper = new ObjectMapper();
            switch (serverType) {
                case AS:
                    applicationNumber = AS_REQUEST_MESSSAGE_TYPE;
                    requestJsonString = objectMapper.writeValueAsString(requestToAs);
                    break;
                case TGS:
                    applicationNumber = TGS_REQUEST_MESSSAGE_TYPE;
                    requestJsonString = objectMapper.writeValueAsString(requestToTgs);
                    break;
                case AP:
                    applicationNumber = AP_REQUEST_MESSAGE_TYPE;
                    requestJsonString = objectMapper.writeValueAsString(requestToAp);
                    break;
            }

            /* Convert string to byte array */
            byte[] requestData = requestJsonString.getBytes(StandardCharsets.UTF_8);

            /* Enclose this message bodu inside the KrbMessage */
            ImmutableKrbMessage krbMessageRequest = ImmutableKrbMessage.builder()
                    .applicationNumber(applicationNumber)
                    .krbMessageBody(requestData)
                    .build();

            String krbMessageJsonString = objectMapper.writeValueAsString(krbMessageRequest);

            /* Convert string to byte array */
            byte[] data = krbMessageJsonString.getBytes(StandardCharsets.UTF_8);

            /* Creating a UDP packet */
            DatagramPacket sendingPacket = new DatagramPacket(data, data.length, IPAddress, serverPort);

            // sending UDP packet to the server
            clientSocket.send(sendingPacket);

            byte[] receivingDataBuffer = new byte[10000];

            // Get the server response
            DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            clientSocket.receive(receivingPacket);

            byte[] krbMessageReceivedData = receivingPacket.getData();
            String krbMessageReceivedString = new String(krbMessageReceivedData, StandardCharsets.UTF_8);

            /* Deserialization of json string to object (KrbMessage) */
            KrbMessage krbMessageReceived = objectMapper.readValue(krbMessageReceivedString, KrbMessage.class);

            if (krbMessageReceived.applicationNumber() == KRB_ERROR_MESSAGE_TYPE) {
                // Error
                switch (serverType) {
                    case AS:
                        errorReplyFromAs = objectMapper.readValue(krbMessageReceivedString, ImmutableKrbError.class);
                        break;
                    case TGS:
                        errorReplyFromTgs = objectMapper.readValue(krbMessageReceivedString, ImmutableKrbError.class);
                        break;
                    case AP:
                        errorReplyFromAp = objectMapper.readValue(krbMessageReceivedString, ImmutableKrbError.class);
                        break;
                }
                System.out.println();
                return KRB_ERROR_MESSAGE_TYPE;

            } else {
                /* Deserialization of json string to object */
                switch (serverType) {
                    case AS:
                        replyFromAs = objectMapper.readValue(krbMessageReceivedString, ImmutableKrbKdcRep.class);
                        break;
                    case TGS:
                        replyFromTgs = objectMapper.readValue(krbMessageReceivedString, ImmutableKrbKdcRep.class);
                        break;
                    case AP:
                        replyFromAp = objectMapper.readValue(krbMessageReceivedString, ImmutableKrbApRep.class);
                        break;
                }

                // Closing the socket connection with the server
                clientSocket.close();
                return SUCCESS;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return SUCCESS;
    }

    private void constructAuthenticationServerRequest() {
        nonceROne = generateNonce(32);
        authenticationServerRequestTime = new Timestamp(System.currentTimeMillis());
        KrbKdcReqBody krbKdcReqBody = new KrbKdcReqBody(clientKerberosId,
                TGS_SERVER,
                addDays(authenticationServerRequestTime, 1),
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

            /*
            There are mainly two parts in TGS request:
                - paData:
                    paData[0] = serialized KrbApReq object
                    - KrbApReq object consists of
                        - ticket (TGT) (encrypted using secret key of TGS)
                        - authenticator (encrypted using Session Key)
                - reqBody
                    contains nonce, cname, sname etc
            */

            /* Retrieve TGT from AS reply */
            ticketGrantingTicket = replyFromAs.ticket();

            PrincipalName applicationServerPrincipalName = new PrincipalName(applicationServerKerberosId);
            ticketGrantingServerRequestTime = new Timestamp(System.currentTimeMillis());

            /* Create Authenticator object */
            UnencryptedAuthenticator authenticatorForTgsReq = new UnencryptedAuthenticator(
                    AUTHENTICATOR_VERSION_NUMBER,
                    clientKerberosId,
                    ticketGrantingServerRequestTime
            );

            /* Serialize Authenticator object into JSON string */
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonForUnencryptedAuthenticator = objectMapper.writeValueAsString(authenticatorForTgsReq);

            /* Encrypt Authenticator JSON string using session key with TGS */
            EncryptionData encryptionDataForAuthenticator = PrivateKeyEncryptor.getEncryptionUsingSecretKey(
                    jsonForUnencryptedAuthenticator, sessionKeyWithTgs);

            /* Create the EncryptedData object for Authenticator to attach in KrbApReq object */
            EncryptedData encryptedDataForAuthenticator = new EncryptedData(
                    1, 1,
                    encryptionDataForAuthenticator.getIv(),
                    encryptionDataForAuthenticator.getCipherText().getBytes(StandardCharsets.UTF_8));

            /* Create KrbApReq object */
            KrbApReq apReqForTgsPaData = ImmutableKrbApReq.builder()
                    .pvno(KERBEROS_VERSION_NUMBER)
                    .msgType(AP_REQUEST_MESSAGE_TYPE)
                    .ticket(ticketGrantingTicket)
                    .authenticator(encryptedDataForAuthenticator)
                    .build();

            /* Serialize the KrbApReq object and assign it as first element of paData array */
            PaData[] paData = new PaData[1];
            // TODO: Can DER Encoding be used?
            paData[0] = new PaData(PA_TGS_REQ ,
                    objectMapper.writeValueAsString(apReqForTgsPaData).getBytes(StandardCharsets.UTF_8));

            /* Generate a nonce R2 to sent to TGS */
            nonceRTwo = generateNonce(32);

            /* Create TGS request body */
            KrbKdcReqBody krbKdcReqBody = new KrbKdcReqBody(
                    clientKerberosId,
                    applicationServerPrincipalName,
                    addDays(ticketGrantingServerRequestTime, 1),
                    nonceRTwo,
                    1);

            /* Create TGS request object */
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
                    new String(replyFromTgs.encPart().getCipher(), StandardCharsets.UTF_8),
                    null,
                    replyFromTgs.encPart().getIv());
            System.out.println("ciphertext"+new String(replyFromTgs.encPart().getCipher(), StandardCharsets.UTF_8));
            String plainText = PrivateKeyEncryptor.getDecryptionUsingSecretKey(encryptionData, sessionKeyWithTgs);
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

            serviceGrantingTicket = replyFromTgs.ticket();

            PrincipalName applicationServerPrincipalName = new PrincipalName(applicationServerKerberosId);

            applicationServerRequestTime = new Timestamp(System.currentTimeMillis());

            UnencryptedAuthenticator authenticatorForApReq = new UnencryptedAuthenticator(
                    AUTHENTICATOR_VERSION_NUMBER,
                    clientKerberosId,
                    applicationServerRequestTime
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

    private void handleApReply() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, JsonProcessingException, TimestampMismatchException {
        /* Decrypt encrypted part of the AP reply using the session key with service server*/
        EncryptionData encryptionData = new EncryptionData(
                new String(replyFromAp.encPart().getCipher(), StandardCharsets.UTF_8),
                null,
                replyFromAp.encPart().getIv());
        System.out.println("ciphertext_replyFromAp"+new String(replyFromAp.encPart().getCipher(), StandardCharsets.UTF_8));
        String plainText = PrivateKeyEncryptor.getDecryptionUsingSecretKey(encryptionData, sessionKeyWithServiceServer);
        System.out.println(plainText);

        /* See if the Timestamp sent in AP Request is same as the Timestamp present in AP Response */
        ObjectMapper objectMapper = new ObjectMapper();
        EncApRepPart encApRepPart = objectMapper.readValue(plainText, EncApRepPart.class);

        if (encApRepPart.getCtime().equals(applicationServerRequestTime)) {
            // Kerberos Authentication Successful
            System.out.println("Kerberos Authentication is Successful.");
        } else {
            throw new TimestampMismatchException("Timestamp received from AP Reply is mismatched from AP Request timestamp");
        }
    }

    private void printKerberosErrorMessageFromAs() {
        System.out.println("Kerberos Authentication Failed!");
        System.out.println("Error Code " + errorReplyFromAs.eText() + ": " + errorReplyFromAs.eText());
    }

    private void printKerberosErrorMessageFromTgs() {
        System.out.println("Kerberos Authentication Failed!");
        System.out.println("Error Code " + errorReplyFromTgs.eText() + ": " + errorReplyFromTgs.eText());
    }

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, JsonProcessingException, TimestampMismatchException {
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
        int ret = client.sendRequestToServerAndReceiveResponse(KDC_SERVICE_PORT, ServerType.AS);
        if (ret == KRB_ERROR_MESSAGE_TYPE) {
            client.printKerberosErrorMessageFromAs();
            exit(1);
        }
        /* TODO: check if user is successfully authenticated */
        client.handleAsReply();

        System.out.println("Authentication Successful!");

        System.out.println("Please enter the kerberos id of the service you would like to access");
        System.out.print("service kerberos id: ");
        client.applicationServerKerberosId = myObj.nextLine();

        /* TGS Exchange */
        client.constructTicketGrantingServerRequest(client.applicationServerKerberosId);
        ret = client.sendRequestToServerAndReceiveResponse(KDC_SERVICE_PORT, ServerType.TGS);
        if (ret == KRB_ERROR_MESSAGE_TYPE) {
            client.printKerberosErrorMessageFromTgs();
            exit(1);
        }
        client.handleTgsReply();

        /* AP Exchange */
        client.constructApplicationServerRequest();
        client.sendRequestToServerAndReceiveResponse(AP_SERVICE_PORT, ServerType.AP);
        client.handleApReply();

    }
}
