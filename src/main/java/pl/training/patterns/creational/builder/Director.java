package pl.training.patterns.creational.builder;

public class Director {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Director.class.getName());
    private final ConnectionUrlBuilder connectionUrlBuilder;

    public void run() {
        var connectionUrl = connectionUrlBuilder.build();
        log.info("Connection url: " + connectionUrl);
    }

    public Director(final ConnectionUrlBuilder connectionUrlBuilder) {
        this.connectionUrlBuilder = connectionUrlBuilder;
    }
}
