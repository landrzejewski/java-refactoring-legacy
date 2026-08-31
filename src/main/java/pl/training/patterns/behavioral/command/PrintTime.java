package pl.training.patterns.behavioral.command;

import java.time.LocalDateTime;

public class PrintTime implements Command {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(PrintTime.class.getName());

    @Override
    public void execute() {
        log.info(LocalDateTime.now().toString());
    }
}
