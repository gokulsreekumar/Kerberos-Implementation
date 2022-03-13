package entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import messageformats.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static utils.Constants.*;

public class AuthenticationServer {
    /* The server port to which
    the client socket is going to connect */
    public final static int SERVICE_PORT = 50001;
    private KrbKdcReq clientRequest;
    private KrbKdcRep replyForClient;


    public static void main(String[] args) {
        AuthenticationServer authenticationServer = new AuthenticationServer();
        while (true) {
            authenticationServer.receiveClientRequestAndReply();
        }
    }

    public void receiveClientRequestAndReply() {
        try {
            /* Instantiate a new DatagramSocket to receive responses from the client */
            DatagramSocket serverSocket = new DatagramSocket(SERVICE_PORT);

            /*
             * Instantiate a UDP packet to store the client data using the buffer for receiving data
             */
            byte[] receivingDataBuffer = new byte[1024];

            System.out.println("Waiting for client request...");

            /* Receive data from the client and store in inputPacket */
            DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            serverSocket.receive(inputPacket);

            /* get the data bytes from inputPacket and convert byte array to json string */
            byte[] dataReceived = inputPacket.getData();
            String dataString = new String(dataReceived);

            /* Deserialization of json string to object */
            ObjectMapper objectMapper = new ObjectMapper();
            KrbKdcReq deserializedClientRequest = objectMapper.readValue(dataString, KrbKdcReq.class);
            System.out.println(deserializedClientRequest.toString());

            /* Obtain client's IP address and the port */
            InetAddress senderAddress = inputPacket.getAddress();
            int senderPort = inputPacket.getPort();

            // TODO: Add logic for verification of client's identity and other AS functions
            constructReplyForClient(deserializedClientRequest);

            /* Serialization of reply object into json string */
            objectMapper = new ObjectMapper();
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

    public void constructReplyForClient(KrbKdcReq krbKdcReq) {
        // TODO: Do the real encryption and decryption
        String sampleString = "Hello World";
        byte[] sampleCipher = sampleString.getBytes();

        EncryptedData sampleEncData = new EncryptedData(1, KERBEROS_VERSION_NUMBER, sampleCipher);
        Ticket sampleTicketTGS = new Ticket(AS_SERVER, sampleEncData);

        replyForClient = ImmutableKrbKdcRep.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(AS_REPLY_MESSSAGE_TYPE)
                .cname(krbKdcReq.reqBody().getCname())
                .ticket(sampleTicketTGS)
                .encPart(sampleEncData)
                .build();
    }

}
