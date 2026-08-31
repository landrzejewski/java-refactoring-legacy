package pl.training.patterns.behavioral.command;

public class ConnectToServer implements Command {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(ConnectToServer.class.getName());

    @Override
    public void execute() {
        log.info("Connecting...");
        log.info("Connected...");
    }
}
