package entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import messageformats.*;
import utils.UserAuthData;

import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import static utils.Constants.*;

public class AuthenticationServer {
    /* The server port to which
    the client socket is going to connect */
    public final static int SERVICE_PORT = 50001;
    private static ArrayList<UserAuthData> userAuthDatabase;
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
            clientRequest = objectMapper.readValue(dataString, KrbKdcReq.class);
            System.out.println(clientRequest.toString());

            /* Obtain client's IP address and the port */
            InetAddress senderAddress = inputPacket.getAddress();
            int senderPort = inputPacket.getPort();

            // TODO: Add logic for verification of client's identity and other AS functions
            loadUserAuthData();

            String clientPassword = getClientCredentials(clientRequest.reqBody().getCname().getNameString());

            constructReplyForClient(clientPassword);

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

    public void constructReplyForClient(String clientPassword) {
        // TODO: Do the real encryption and decryption
        String sampleString = "Hello World";
        byte[] sampleCipher = sampleString.getBytes();

        EncryptedData sampleEncData = new EncryptedData(1, KERBEROS_VERSION_NUMBER, sampleCipher);
        Ticket sampleTicketTGS = new Ticket(AS_SERVER, sampleEncData);

        // Encrypted part and Ticket
        // Encrypted part uses Client's Private Key
        // Ticket uses TGS's Private Key for its Encrypted Part

        PaData[] paData = new PaData[0];
        replyForClient = ImmutableKrbKdcRep.builder()
                .pvno(KERBEROS_VERSION_NUMBER)
                .msgType(AS_REPLY_MESSSAGE_TYPE)
                .paData(paData)
                .cname(clientRequest.reqBody().getCname())
                .ticket(sampleTicketTGS)
                .encPart(sampleEncData)
                .build();
    }

    public void loadUserAuthData() throws IOException, CsvValidationException {
        // Create a fileReader object
        FileReader fileReader = new FileReader("/Users/gokul/Developer/Kerberos-Implementation/KerberosAuthentication/src/main/java/resources/ClientAuthenticationDatabase.csv");

        // Create a csvReader object by passing fileReader as parameter
        CSVReader csvReader = new CSVReader(fileReader);
        String[] nextRecord;

        ArrayList<UserAuthData> allUserAuthData = new ArrayList<>();
        // Reading client record line by line
        while ((nextRecord = csvReader.readNext()) != null) {
            UserAuthData oneUserAuthData = new UserAuthData();
            for (int _i = 0; _i < 2; _i++) {
                System.out.print(nextRecord[_i] + "\t");
                if (_i == 0) {
                    oneUserAuthData.setUserid(nextRecord[_i]);
                } else if (_i == 1) {
                    oneUserAuthData.setPassword(nextRecord[_i]);
                }
            }
            allUserAuthData.add(oneUserAuthData);
            System.out.println();
        }
        userAuthDatabase = allUserAuthData;
    }

    public String getClientCredentials(String clientUserid) {
        for (UserAuthData user : userAuthDatabase) {
            if (user.getUserid() == clientUserid) {
                return user.getPassword();
            }
        }
        return "USER_NOT_FOUND";
    }
}
