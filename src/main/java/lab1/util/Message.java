package lab1.util;

public class Message {

    public final int clientName;
    public final MessageType type;
    public final String value;

    public Message(int clientName, MessageType type, String value) {
        this.clientName = clientName;
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return clientName + ": " + value;
    }
}
