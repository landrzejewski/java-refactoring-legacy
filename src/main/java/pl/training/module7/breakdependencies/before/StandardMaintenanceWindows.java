package pl.training.module7.breakdependencies.before;

public final class StandardMaintenanceWindows {
    private static final int WINDOW_START_HOUR_UTC = 0;
    private static final int WINDOW_END_HOUR_UTC = 6;

    public boolean allows(String service, int hourUtc) {
        return hourUtc >= WINDOW_START_HOUR_UTC
                && hourUtc < WINDOW_END_HOUR_UTC;
    }
}
