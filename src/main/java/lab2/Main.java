package lab2;

public class Main {
    public static void main(String[] args) {
        SimpleStringMap map = new DistributedMap();
        Repl repl = new Repl(map);
        repl.run();
    }
}
