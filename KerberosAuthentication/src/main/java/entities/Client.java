package entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import messageformats.*;

import java.net.*;
import java.sql.Timestamp;
import java.util.Random;
import java.util.Scanner;

import static entities.AuthenticationServer.SERVICE_PORT;
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
    private ImmutableKrbKdcReq asRequest;
    private KrbKdcRep replyFromAs;

    public Client(PrincipalName clientKerberosId, String loginPassword) {
        this.clientKerberosId = clientKerberosId;
        this.loginPassword = loginPassword;
    }

    public static void main(String[] args) {
        System.out.println("WELCOME TO KERBEROS AUTHENTICATION SYSTEM!");
        System.out.println("Please enter your kerberos id and password to get started");
        Scanner myObj = new Scanner(System.in);

        System.out.print("kerberos id: ");
        String kerberosId = myObj.nextLine();
        System.out.print("password: ");
        String password = myObj.nextLine();

        Client client = new Client(new PrincipalName(kerberosId), password);

        client.constructAuthenticationServerRequest();
        client.sendRequestToAsAndReceiveResponse();
    }

    public void sendRequestToAsAndReceiveResponse() {
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
            String json = objectMapper.writeValueAsString(asRequest);

            /* Convert string to byte array */
            byte[] data = json.getBytes();

            /* Creating a UDP packet */
            DatagramPacket sendingPacket = new DatagramPacket(data, data.length, IPAddress, SERVICE_PORT);

            // sending UDP packet to the server
            clientSocket.send(sendingPacket);

            byte[] receivingDataBuffer = new byte[1024];

            // Get the server response
            DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            clientSocket.receive(receivingPacket);

            byte[] dataReceived = receivingPacket.getData();
            String dataString = new String(dataReceived);

            /* Deserialization of json string to object */
            objectMapper = new ObjectMapper();
            replyFromAs = objectMapper.readValue(dataString, KrbKdcRep.class);
            System.out.println(replyFromAs.toString());

            // Closing the socket connection with the server
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void constructAuthenticationServerRequest() {
        KrbKdcReqBody krbKdcReqBody = new KrbKdcReqBody(clientKerberosId,
                TGS_SERVER,
                addDays(new Timestamp(System.currentTimeMillis()), 1),
                generateNonce(32),
                1);

        asRequest = ImmutableKrbKdcReq.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(AS_REQUEST_MESSSAGE_TYPE)
                .reqBody(krbKdcReqBody)
                .build();
    }
}
