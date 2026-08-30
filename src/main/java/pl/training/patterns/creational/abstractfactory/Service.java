package pl.training.patterns.creational.abstractfactory;

public class Service {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Service.class.getName());
    private final ConnectionAbstractFactory connectionAbstractFactory;

    public void run() {
        var connection = connectionAbstractFactory.createConnection();
        var securedConnection = connectionAbstractFactory.createSecuredConnection();
        log.info("Connection: " + connection.getPort());
        log.info("Secured connection: " + securedConnection.getPort());
    }

    public Service(final ConnectionAbstractFactory connectionAbstractFactory) {
        this.connectionAbstractFactory = connectionAbstractFactory;
    }
}
