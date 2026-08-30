package pl.training.patterns.creational.factorymethod;

public class Service {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Service.class.getName());
    private final IdGeneratorFactory idGeneratorFactory;

    public void run() {
        var idGenerator = idGeneratorFactory.create();
        log.info("Id: " + idGenerator.getNext());
    }

    public Service(final IdGeneratorFactory idGeneratorFactory) {
        this.idGeneratorFactory = idGeneratorFactory;
    }
}
