package lab2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Repl {
    private final SimpleStringMap map;
    private final BufferedReader bufferedReader;

    Repl(SimpleStringMap map) {
        this.map = map;
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run() {
        while (true) {

            try {
                System.out.print("$ ");
                String command = bufferedReader.readLine();
                String[] splittedCommand = command.split(" ");
                String[] args = Arrays.copyOfRange(splittedCommand, 1, splittedCommand.length);
                switch (splittedCommand[0]) {
                    case "put":
                        handlePut(args);
                        break;
                    case "delete":
                        handleDelete(args);
                        break;
                    case "get":
                        handleGet(args);
                        break;
                    case "has":
                        handleHas(args);
                        break;
                    case "quit":
                        return;
                    default:
                        throw new IOException("Use put,delete,get,has,quit option!");
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handleHas(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IOException("has use 1 argument");
        }
        String key = args[0];
        Boolean exists = map.containsKey(key);
        System.out.println("Has: " + key);
        if (exists) {
            System.out.println("Key: " + key + " is present");
        } else {
            System.out.println("Key: " + key + " is not present");
        }

    }

    private void handleGet(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IOException("get use 1 argument");
        }
        String key = args[0];
        String value = map.get(key);
        System.out.println("Get: " + key);
        System.out.println("Current value of: " + key + " is: " + value);
    }

    private void handleDelete(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IOException("delete use 1 argument");
        }
        String key = args[0];
        String previousValue = map.remove(key);
        System.out.println("Deleted: " + key);
        System.out.println("Previous value of: " + key + " was: " + previousValue);
    }

    private void handlePut(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IOException("put use 2 arguments");
        }
        String key = args[0];
        String value = args[1];
        String previousValue = map.put(key, value);
        System.out.println("Put: " + key + " with value: " + value);
        System.out.println("Previous value of: " + key + " was: " + previousValue);
    }

}
