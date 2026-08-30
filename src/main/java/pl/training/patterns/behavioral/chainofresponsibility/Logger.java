package pl.training.patterns.behavioral.chainofresponsibility;

public class Logger extends Handler {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Logger.class.getName());

    public Logger(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handleRequest(String request) {
        log.info(request);
        nextHandler.handleRequest(request);
    }
}
