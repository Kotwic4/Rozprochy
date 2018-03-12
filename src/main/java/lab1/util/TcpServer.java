package lab1.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {

    private ServerSocket serverSocket;

    public TcpServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static TcpServer bootServer(int portNumber) throws IOException {
        ServerSocket serverSocket = new ServerSocket(portNumber);
        return new TcpServer(serverSocket);
    }

    public TcpConnection getClient() throws IOException {
        Socket clientSocket = serverSocket.accept();
        return TcpConnection.fromSocket(clientSocket)
                .orElseThrow(() -> new RuntimeException("Bład połaczenia z clientem"));
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
