package lab1.util;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

public class TcpConnection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson;

    private TcpConnection(Socket socket, BufferedReader in, PrintWriter out, Gson gson) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.gson = gson;
    }

    public static Optional<TcpConnection> connectToServer(String hostName, int portNumber) throws IOException {
        Socket socket = new Socket(hostName, portNumber);
        return fromSocket(socket);
    }

    public static Optional<TcpConnection> fromSocket(Socket socket) throws IOException {
        TcpConnection tcpConnection;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            tcpConnection = new TcpConnection(socket, in, out, new Gson());
        } catch (IOException e) {
            e.printStackTrace();
            socket.close();
            tcpConnection = null;
        }
        return Optional.ofNullable(tcpConnection);
    }

    public void close() throws IOException {
        socket.close();
    }

    public void send(String msg) {
        out.println(msg);
    }

    public void send(Message message) {
        String msg = gson.toJson(message);
        send(msg);
    }

    public String recv() throws IOException {
        return in.readLine();
    }

    public Message recvMsg() throws IOException {
        return gson.fromJson(recv(), Message.class);
    }

}
