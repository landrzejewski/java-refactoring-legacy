package pl.training.patterns.behavioral.strategy;

public class Service {
    private final IdGenerator idGenerator;

    public void run() {
        var id = idGenerator.getNext();
    }

    public Service(final IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}
