package pl.training.patterns.behavioral.chainofresponsibility;

public class Processor extends Handler {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Processor.class.getName());

    @Override
    public void handleRequest(String request) {
        log.info("Processing: " + request);
    }
}
