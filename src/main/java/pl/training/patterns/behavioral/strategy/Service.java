package pl.training.patterns.behavioral.strategy;

public class Service {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Service.class.getName());
    private final IdGenerator idGenerator;

    public void run() {
        log.info("Id: " + idGenerator.getNext());
    }

    public Service(final IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}
