package pl.training.module7.breakdependencies.after;

@FunctionalInterface
public interface MaintenanceWindows {
    boolean allows(String service, int hourUtc);
}
