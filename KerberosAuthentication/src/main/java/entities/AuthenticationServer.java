package entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import messageformats.ImmutableKrbKdcReq;
import messageformats.KrbKdcReq;
import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class AuthenticationServer {
    /* The server port to which
    the client socket is going to connect */
    public final static int SERVICE_PORT = 50001;
    private KrbKdcReq clientRequest;

    public void receiveClientRequest(){
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

//            /*
//             * Convert client sent data string to upper case,
//             * Convert it to bytes and store it in the corresponding buffer.
//             */
//            sendingDataBuffer = receivedData.toUpperCase().getBytes();

//            /* Obtain client's IP address and the port */
//            InetAddress senderAddress = inputPacket.getAddress();
//            int senderPort = inputPacket.getPort();
//
//            /* Create new UDP packet with data to send to the client */
//            DatagramPacket outputPacket = new DatagramPacket(
//                    sendingDataBuffer, sendingDataBuffer.length,
//                    senderAddress, senderPort
//            );
//
//            /* Send the created packet to client */
//            serverSocket.send(outputPacket);

            /* Close the socket connection */
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
}

    public void constructClientReply(){

    }

    public void sendReplyToClient(){

    }

    public static void main(String[] args) {
        AuthenticationServer authenticationServer = new AuthenticationServer();
        authenticationServer.receiveClientRequest();
//        authenticationServer.constructClientReply();
//        authenticationServer.sendReplyToClient();
    }
}
