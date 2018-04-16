package lab3;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Doctor extends QueueClient {

    private String id;

    private void getId() throws IOException {
        System.out.println("id:");
        id = br.readLine();
    }

    private void initDoctorQueue() throws IOException {
        String doctorQueue = channel.queueDeclare().getQueue();
        System.out.println("created id queue: " + doctorQueue);
        channel.queueBind(doctorQueue, Constants.EXCHANGE_NAME_TOPIC, id);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);
                String[] splittedMessage = message.split(" ");
                System.out.println("Received " + splittedMessage[1] + " results for " + splittedMessage[0]);
            }
        };
        channel.basicConsume(doctorQueue, true, consumer);
    }

    private void initQueue() throws IOException {
        initInfoQueue();
        initDoctorQueue();
        System.out.println("Waiting for messages...");
    }

    public void run() throws IOException, TimeoutException {
        System.out.println("Doctor");
        getId();
        initChannel();
        initQueue();
        while (true) {
            System.out.println("Enter message: <type> <name>");
            String message = br.readLine();
            if ("exit".equals(message)) {
                break;
            }
            String[] msg = message.split(" ");
            String key = id + "." + msg[0];
            channel.basicPublish(Constants.EXCHANGE_NAME_TOPIC, key, null, message.getBytes());
            System.out.println("Sent: " + message);
        }
    }

    public static void main(String[] argv) throws Exception {
        Doctor doctor = new Doctor();
        doctor.run();
    }
}
