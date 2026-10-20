package pl.training.module3.breakdependencies.after;

@FunctionalInterface
public interface MaintenanceWindows {
    boolean allows(String service, int hourUtc);
}
