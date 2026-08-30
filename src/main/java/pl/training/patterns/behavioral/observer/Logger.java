package pl.training.patterns.behavioral.observer;

import java.util.function.Consumer;

public class Logger implements Consumer<ServerEvent> {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Logger.class.getName());

    @Override
    public void accept(ServerEvent serverEvent) {
        log.info(serverEvent.getPayload());
    }
}
