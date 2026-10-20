package pl.training.module5.variability.after;

/**
 * Command: żądanie operatora jako obiekt, który można zakolejkować, zalogować i cofnąć.
 * Wykonanie i cofnięcie są obok siebie, a nie w dwóch osobnych {@code switch}-ach.
 */
public sealed interface OperatorCommand {
    String name();

    void execute(Cluster cluster);

    void undo(Cluster cluster);

    static OperatorCommand byName(String action) {
        return switch (action) {
            case "scale-up" -> new ScaleUp();
            case "scale-down" -> new ScaleDown();
            case "maintenance" -> new ToggleMaintenance();
            case null, default -> throw new IllegalArgumentException("unknown action: " + action);
        };
    }

    record ScaleUp() implements OperatorCommand {
        @Override
        public String name() {
            return "scale-up";
        }

        @Override
        public void execute(Cluster cluster) {
            cluster.addInstance();
        }

        @Override
        public void undo(Cluster cluster) {
            cluster.removeInstance();
        }
    }

    record ScaleDown() implements OperatorCommand {
        @Override
        public String name() {
            return "scale-down";
        }

        @Override
        public void execute(Cluster cluster) {
            cluster.removeInstance();
        }

        @Override
        public void undo(Cluster cluster) {
            cluster.addInstance();
        }
    }

    record ToggleMaintenance() implements OperatorCommand {
        @Override
        public String name() {
            return "maintenance";
        }

        @Override
        public void execute(Cluster cluster) {
            cluster.toggleMaintenance();
        }

        @Override
        public void undo(Cluster cluster) {
            cluster.toggleMaintenance();
        }
    }
}
