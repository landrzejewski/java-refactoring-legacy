package pl.training.workshop.exercises.e10_showtime.step1;

/** Krok 1: nagłośnienie z limitem głośności (ochrona słuchu) - limit jest logiką urządzenia, nie sali. */
public final class Sound {
    public static final int MAX_LEVEL = 85;

    private final DeviceLog log;
    private int level;

    public Sound(DeviceLog log) {
        this.log = log;
    }

    public void set(int requested) {
        int applied = Math.min(requested, MAX_LEVEL);
        level = applied;
        log.add(applied < requested ? "sound: " + applied + " (capped)" : "sound: " + applied);
    }

    public int level() {
        return level;
    }
}
