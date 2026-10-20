package pl.training.module5.variability.after;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Invoker: kolejkuje, wykonuje, loguje i cofa polecenia, nie znając ich treści.
 */
public final class CommandConsole {
    private final Cluster cluster = new Cluster();
    private final Deque<OperatorCommand> queue = new ArrayDeque<>();
    private final Deque<OperatorCommand> history = new ArrayDeque<>();
    private final List<String> log = new ArrayList<>();

    public void submit(OperatorCommand command) {
        queue.addLast(Objects.requireNonNull(command, "command"));
    }

    public void runQueued() {
        while (!queue.isEmpty()) {
            OperatorCommand command = queue.removeFirst();
            command.execute(cluster);
            history.push(command);
            log.add(command.name() + " -> " + cluster);
        }
    }

    public void undoLast() {
        if (history.isEmpty()) {
            throw new IllegalStateException("nothing to undo");
        }
        OperatorCommand command = history.pop();
        command.undo(cluster);
        log.add("undo " + command.name() + " -> " + cluster);
    }

    public List<String> log() {
        return List.copyOf(log);
    }

    public Cluster cluster() {
        return cluster;
    }
}
