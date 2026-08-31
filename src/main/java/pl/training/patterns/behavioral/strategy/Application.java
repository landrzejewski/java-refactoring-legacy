package pl.training.patterns.behavioral.strategy;

public class Application {

    public static void main(String[] args) {
        new Service(new UuidGenerator()).run();
        new Service(new IncrementalIdGenerator()).run();
    }

}
