package lab1.server;

import lab1.util.TcpConnection;
import lab1.util.TcpServer;

import java.io.IOException;

public class TcpClientCollector extends Thread {

    private TcpServer tcpServer;
    private ConnectionManager connectionManager;

    public TcpClientCollector(TcpServer tcpServer, ConnectionManager connectionManager) {
        this.tcpServer = tcpServer;
        this.connectionManager = connectionManager;
    }

    @Override
    public void run() {
        try {
            while (true) {
                TcpConnection tcpConnection = tcpServer.getClient();
                connectionManager.addClient(tcpConnection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
