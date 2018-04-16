package lab3;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Admin extends QueueClient {

    private void initQueue() throws IOException {
        String adminQueue = channel.queueDeclare().getQueue();
        System.out.println("created queue: " + adminQueue);
        channel.queueBind(adminQueue, Constants.EXCHANGE_NAME_TOPIC, "#");
        channel.basicConsume(adminQueue, true, infoConsumer);
        System.out.println("Waiting for messages...");
    }

    public void run() throws IOException, TimeoutException {
        System.out.println("Admin");
        initChannel();
        initQueue();
        while (true) {
            System.out.println("Enter message: ");
            String message = br.readLine();
            if ("exit".equals(message)) {
                break;
            }
            channel.basicPublish(Constants.EXCHANGE_NAME_INFO, "", null, message.getBytes());
            System.out.println("Sent: " + message);
        }
    }

    public static void main(String[] argv) throws IOException, TimeoutException {
        Admin admin = new Admin();
        admin.run();
    }
}
