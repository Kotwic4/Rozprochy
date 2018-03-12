package lab1.util;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UdpConnection{

    private DatagramSocket socket;

    private InetAddress address;

    private int portNumber;

    public UdpConnection(DatagramSocket socket, int portNumber, InetAddress address) {
        this.socket = socket;
        this.address = address;
        this.portNumber = portNumber;
    }

    public static UdpConnection connect(int portNumber, String host) throws SocketException, UnknownHostException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(host);
        return new UdpConnection(socket,portNumber,address);
    }

    public static UdpConnection connect(int portNumber) throws SocketException, UnknownHostException {
        return connect(portNumber,"localhost");
    }

    public void close() {
        socket.close();
    }

    public void send(String msg) throws IOException {
        byte[] sendBuffer = msg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
        socket.send(sendPacket);
    }

    public String recv() throws IOException {
        byte[] receiveBuffer = new byte[1024];
        Arrays.fill(receiveBuffer, (byte)0);
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        return new String(receivePacket.getData());
    }
}
