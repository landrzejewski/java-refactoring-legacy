package pl.training.patterns.behavioral.state;

public class Application {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Application.class.getName());

    public static void main(String[] args) {
        var order = new Order();
        log.info("State: " + order.getState());
        order.pay();
        log.info("State: " + order.getState());
        order.ship();
        log.info("State: " + order.getState());
        try {
            order.cancel();
        } catch (IllegalStateException exception) {
            log.info(exception.getMessage());
        }
    }

}
