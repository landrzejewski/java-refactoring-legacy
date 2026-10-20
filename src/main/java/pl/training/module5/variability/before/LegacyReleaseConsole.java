package pl.training.module5.variability.before;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import pl.training.module5.variability.CustomerTier;
import pl.training.module5.variability.ReleaseStatus;

/**
 * Jedna klasa, trzy różne osie zmienności rozwiązane tym samym narzędziem
 * ({@code switch}/{@code if}): taryfa klienta, polecenia operatora i cykl życia wydania.
 */
public final class LegacyReleaseConsole {
    private static final Set<String> ACTIONS = Set.of("scale-up", "scale-down", "maintenance");

    private final CustomerTier tier;
    private final Deque<String> queue = new ArrayDeque<>();
    private final Deque<String> history = new ArrayDeque<>();
    private final List<String> log = new ArrayList<>();
    private int instances = 2;
    private boolean maintenance;
    private ReleaseStatus status = ReleaseStatus.PLANNED;

    public LegacyReleaseConsole(CustomerTier tier) {
        this.tier = Objects.requireNonNull(tier, "tier");
    }

    // (a) taryfa jest stała dla klienta, a mimo to wybierana przy każdym wywołaniu
    public long supportCost(int hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("hours must not be negative");
        }
        return switch (tier) {
            case STANDARD -> hours * 100L;
            case PREMIUM -> 500 + hours * 80L;
            case ENTERPRISE -> Math.max(2_000, hours * 60L);
        };
    }

    // (b) żądanie operatora to tylko nazwa - kolejkowanie, log i cofanie znają ją osobno
    public void submit(String action) {
        if (!ACTIONS.contains(action)) {
            throw new IllegalArgumentException("unknown action: " + action);
        }
        queue.addLast(action);
    }

    public void runQueued() {
        while (!queue.isEmpty()) {
            String action = queue.removeFirst();
            switch (action) {
                case "scale-up" -> instances++;
                case "scale-down" -> {
                    if (instances == 1) {
                        throw new IllegalStateException("cannot scale below one instance");
                    }
                    instances--;
                }
                case "maintenance" -> maintenance = !maintenance;
                default -> throw new IllegalArgumentException("unknown action: " + action);
            }
            history.push(action);
            log.add(action + " -> " + cluster());
        }
    }

    public void undoLast() {
        if (history.isEmpty()) {
            throw new IllegalStateException("nothing to undo");
        }
        String action = history.pop();
        switch (action) {
            case "scale-up" -> instances--;
            case "scale-down" -> instances++;
            case "maintenance" -> maintenance = !maintenance;
            default -> throw new IllegalArgumentException("unknown action: " + action);
        }
        log.add("undo " + action + " -> " + cluster());
    }

    public List<String> log() {
        return List.copyOf(log);
    }

    // (c) zachowanie zależy od statusu, który zmienia się w czasie
    public ReleaseStatus status() {
        return status;
    }

    public void advance() {
        status = switch (status) {
            case PLANNED -> ReleaseStatus.DEPLOYING;
            case DEPLOYING -> ReleaseStatus.LIVE;
            case LIVE, ROLLED_BACK -> throw new IllegalStateException(
                    "cannot advance release in state " + status);
        };
    }

    public void rollback() {
        if (status == ReleaseStatus.DEPLOYING || status == ReleaseStatus.LIVE) {
            status = ReleaseStatus.ROLLED_BACK;
            return;
        }
        throw new IllegalStateException("cannot roll back release in state " + status);
    }

    public String banner() {
        return switch (status) {
            case PLANNED -> "release planned";
            case DEPLOYING -> "deployment in progress";
            case LIVE -> "release is live";
            case ROLLED_BACK -> "release rolled back";
        };
    }

    private String cluster() {
        return "instances=" + instances + ", maintenance=" + (maintenance ? "on" : "off");
    }
}
