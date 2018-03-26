package lab2;

public class Main {
    public static void main(String[] args) {
        try {
            SimpleStringMap map = new DistributedMap("myDistributedMap", "230.0.0.101");
            Repl repl = new Repl(map);
            repl.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
