package entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import messageformats.ImmutableKrbKdcReq;
import messageformats.KrbKdcReqBody;
import messageformats.PrincipalName;

import java.net.*;
import java.sql.Timestamp;

import static entities.AuthenticationServer.SERVICE_PORT;
import static utils.Constants.*;
import static utils.Helpers.addDays;
import static utils.Helpers.generateNonce;

public class Client {
    /* The server port to which
    the client socket is going to connect */
    public static final PrincipalName client = new PrincipalName("client");
    private ImmutableKrbKdcReq asKrbRequest;

    public void sendRequestToAs(){
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
            String json = objectMapper.writeValueAsString(asKrbRequest);

            /* Convert string to byte array */
            byte[] data = json.getBytes();

            /* Creating a UDP packet */
            DatagramPacket sendingPacket = new DatagramPacket(data, data.length, IPAddress, SERVICE_PORT);

            // sending UDP packet to the server
            clientSocket.send(sendingPacket);

//            // Get the server response .i.e. capitalized sentence
//            DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
//            clientSocket.receive(receivingPacket);

//            // Printing the received data
//            String receivedData = byteArrayToString(receivingPacket.getData());
//            System.out.println("Sent from the server: " + receivedData.stripTrailing());

            // Closing the socket connection with the server
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void constructAuthenticationServerRequest(){
        KrbKdcReqBody krbKdcReqBody = new KrbKdcReqBody(
                client,
                tgs_server,
                addDays(new Timestamp(System.currentTimeMillis()),1),
                generateNonce(32),
                1
                );

        asKrbRequest = ImmutableKrbKdcReq.builder()
                .pvno(KerberosVersionNumber)
                .msgType(AsRequestMesssageType)
                .reqBody(krbKdcReqBody)
                .build();
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.constructAuthenticationServerRequest();
        client.sendRequestToAs();
    }
}
