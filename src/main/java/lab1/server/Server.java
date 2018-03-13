package lab1.server;

import com.google.gson.Gson;
import lab1.util.TcpServer;
import lab1.util.UdpServer;

import java.io.IOException;

public class Server {

    public static void main(String[] args) {
        System.out.println("Server started!");
        int portNumber = 12345;
        int connectionLimit = 2;
        try {
            TcpServer tcpServer = TcpServer.bootServer(portNumber);
            UdpServer udpServer = UdpServer.bootServer(portNumber);
            ConnectionManager connectionManager = new ConnectionManager(connectionLimit);
            TcpClientCollector tcpClientCollector = new TcpClientCollector(tcpServer, connectionManager);
            UdpChannel udpChannel = new UdpChannel(udpServer, connectionManager, new Gson());
            tcpClientCollector.start();
            udpChannel.start();
            System.out.println("Listen on TCP on port:" + portNumber);
            System.out.println("Listen on UDP on port:" + portNumber);
            while (true) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}