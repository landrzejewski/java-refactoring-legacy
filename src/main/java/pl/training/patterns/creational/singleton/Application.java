package pl.training.patterns.creational.singleton;

public class Application {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Application.class.getName());

    public static void main(String[] args) {
        var logger = Logger.getInstance();
        log.info("Is same: " + logger.equals(Logger.getInstance()));
        logger.log("Success");
    }
}
