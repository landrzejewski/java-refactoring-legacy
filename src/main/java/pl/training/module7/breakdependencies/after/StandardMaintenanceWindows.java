package pl.training.module7.breakdependencies.after;

public final class StandardMaintenanceWindows implements MaintenanceWindows {
    private static final int WINDOW_START_HOUR_UTC = 0;
    private static final int WINDOW_END_HOUR_UTC = 6;

    @Override
    public boolean allows(String service, int hourUtc) {
        return hourUtc >= WINDOW_START_HOUR_UTC
                && hourUtc < WINDOW_END_HOUR_UTC;
    }
}
