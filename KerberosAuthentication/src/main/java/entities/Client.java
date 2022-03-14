package entities;

import Exceptions.NonceDisMatchException;
import com.fasterxml.jackson.databind.ObjectMapper;
import messageformats.*;
import utils.EncryptionData;
import utils.PrivateKeyEncryptor;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Random;
import java.util.Scanner;

import static entities.AuthenticationServer.SERVICE_PORT;
import static java.lang.System.exit;
import static utils.Constants.*;
import static utils.Helpers.addDays;
import static utils.Helpers.generateNonce;

public class Client {
    /* The server port to which
    the client socket is going to connect */
    public static final PrincipalName client = new PrincipalName("client" + new Random().nextInt(100));
    private PrincipalName clientKerberosId;
    private PrincipalName applicationServerKerberosId;
    private String loginPassword;
    private ImmutableKrbKdcReq requestToAs;
    private ImmutableKrbKdcRep replyFromAs;
    private ImmutableKrbKdcReq requestToTgs;
    private ImmutableKrbKdcRep replyFromTgs;
    private Ticket ticketGrantingTicket;
    private Ticket serviceGrantingTicket;
    private byte[] sessionKeyWithTgs;
    private byte[] SessionKeyWithServiceServer;
    private int nonceROne;
    private int nonceRTwo;

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

    private void sendRequestToAsAndReceiveResponse() {
        try {
            /*
             * Instantiate client socket.
             * No need to bind to a specific port
             */
            DatagramSocket clientSocket = new DatagramSocket();

            /* Get the IP address of the server */
            InetAddress IPAddress = InetAddress.getByName("localhost");

            /* Serialization of object into json string */
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(requestToAs);

            /* Convert string to byte array */
            byte[] data = json.getBytes();

            /* Creating a UDP packet */
            DatagramPacket sendingPacket = new DatagramPacket(data, data.length, IPAddress, SERVICE_PORT);

            // sending UDP packet to the server
            clientSocket.send(sendingPacket);

            byte[] receivingDataBuffer = new byte[5120];

            // Get the server response
            DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            clientSocket.receive(receivingPacket);

            byte[] dataReceived = receivingPacket.getData();
            String dataString = new String(dataReceived);

            /* Deserialization of json string to object */
            replyFromAs = objectMapper.readValue(dataString, ImmutableKrbKdcRep.class);
            System.out.println(replyFromAs.toString());

            // Closing the socket connection with the server
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void constructTicketGrantingServerRequest(){

    }

    private void sendRequestToTgsAndReceiveResponse() {

    }

    private void decryptAsReply() {

        try {
            EncryptionData encryptionData = new EncryptionData(
                    new String(replyFromAs.encPart().getCipher(), StandardCharsets.UTF_8),
                    replyFromAs.paData()[0].getPadataValue(),
                    replyFromAs.paData()[1].getPadataValue());
            System.out.println("ciphertext"+new String(replyFromAs.encPart().getCipher(), StandardCharsets.UTF_8));

            String plainText = null;
            plainText = PrivateKeyEncryptor.getDecryptionUsingPassword(encryptionData, loginPassword);

            System.out.println(plainText);

            ObjectMapper objectMapper = new ObjectMapper();
            EncKdcRepPart encKdcRepPartInAsReply = objectMapper.readValue(plainText, EncKdcRepPart.class);

            sessionKeyWithTgs = encKdcRepPartInAsReply.getKey().getKeyValue();

            int nonceROneReceivedInReply = encKdcRepPartInAsReply.getNonce();

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

    private void retrieveTicketGrantingTicket() {
        ticketGrantingTicket = replyFromAs.ticket();
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
        client.sendRequestToAsAndReceiveResponse();

        /* TODO: check if user is successfully authenticated */
        try {
            client.decryptAsReply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.retrieveTicketGrantingTicket();

        System.out.println("Authentication Successful!");

        System.out.println("Please enter the kerberos id of the service you would like to access");
        System.out.print("service kerberos id: ");
        String applicationServerKerberosId = myObj.nextLine();

        PrincipalName sName = new PrincipalName(applicationServerKerberosId);

        /* TGS Exchange */
        client.constructTicketGrantingServerRequest();
        client.sendRequestToTgsAndReceiveResponse();

    }
}
