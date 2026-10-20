package pl.training.module5.variability.after;

import java.util.List;
import java.util.Objects;

import pl.training.module5.variability.CustomerTier;
import pl.training.module5.variability.ReleaseStatus;

/**
 * Konsola wydania złożona z trzech mechanizmów, po jednym na każdą oś zmienności:
 * Strategy (taryfa wybrana raz przez klienta), Command (żądania operatora jako obiekty)
 * i State (cykl życia, który sam zmienia zachowanie). Kryterium wyboru opisuje
 * {@code package-info} tego pakietu.
 */
public final class ReleaseConsole {
    private final SupportCostPolicy costPolicy;
    private final CommandConsole commands = new CommandConsole();
    private final ReleaseLifecycle lifecycle = new ReleaseLifecycle();

    public ReleaseConsole(SupportCostPolicy costPolicy) {
        this.costPolicy = Objects.requireNonNull(costPolicy, "costPolicy");
    }

    public static ReleaseConsole forTier(CustomerTier tier) {
        return new ReleaseConsole(SupportCostPolicy.forTier(tier));
    }

    public long supportCost(int hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("hours must not be negative");
        }
        return costPolicy.cost(hours);
    }

    public void submit(String action) {
        commands.submit(OperatorCommand.byName(action));
    }

    public void runQueued() {
        commands.runQueued();
    }

    public void undoLast() {
        commands.undoLast();
    }

    public List<String> log() {
        return commands.log();
    }

    public ReleaseStatus status() {
        return lifecycle.status();
    }

    public void advance() {
        lifecycle.advance();
    }

    public void rollback() {
        lifecycle.rollback();
    }

    public String banner() {
        return lifecycle.banner();
    }
}
