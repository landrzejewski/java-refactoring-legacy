package pl.training.workshop.exercises.e10_showtime.step1;

/** Krok 1: oświetlenie sali. Jedyne urządzenie bez referencji do innych - reaguje tylko na polecenia. */
public final class Lights {
    private final DeviceLog log;
    private int level = 100;

    public Lights(DeviceLog log) {
        this.log = log;
    }

    public void set(int level) {
        if (level < 0 || level > 100) {
            throw new IllegalArgumentException("lights level out of range: " + level);
        }
        this.level = level;
        log.add("lights: " + level + "%");
    }

    public int level() {
        return level;
    }
}
