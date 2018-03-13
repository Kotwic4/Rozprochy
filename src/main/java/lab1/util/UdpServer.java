package lab1.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class UdpServer{

    private DatagramSocket socket;

    public UdpServer(DatagramSocket socket) {
        this.socket = socket;
    }

    public static UdpServer bootServer(int portNumber) throws SocketException {
        DatagramSocket socket = new DatagramSocket(portNumber);
        return new UdpServer(socket);
    }

    public DatagramPacket recv() throws IOException {
        byte[] receiveBuffer = new byte[1024];
        Arrays.fill(receiveBuffer, (byte)0);
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        return receivePacket;
    }

    public void close() {
        socket.close();
    }
}
