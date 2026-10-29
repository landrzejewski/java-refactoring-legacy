package pl.training.workshop.exercises.e10_showtime.start;

/**
 * Start: odtwarzacz reklam. Gdy skończy playlistę, sam gasi światła, ustawia dźwięk i uruchamia projektor,
 * więc musi znać trzy inne urządzenia i tytuł filmu.
 */
public final class AdPlayer {
    private final DeviceLog log;
    private Lights lights;
    private Sound sound;
    private Projector projector;
    private int remaining;
    private String title;

    public AdPlayer(DeviceLog log) {
        this.log = log;
    }

    public void wire(Lights lights, Sound sound, Projector projector) {
        this.lights = lights;
        this.sound = sound;
        this.projector = projector;
    }

    public void start(int ads, String title) {
        remaining = ads;
        this.title = title;
        log.add("ads: start " + ads);
    }

    /** Sprzęt: kolejna reklama się skończyła. */
    public void tick() {
        if (remaining <= 0) {
            throw new IllegalStateException("no ads playing");
        }
        remaining--;
        if (remaining > 0) {
            log.add("ads: " + remaining + " left");
            return;
        }
        log.add("ads: finished");
        lights.set(0);
        sound.set(70);
        projector.play(title);
        title = null;
    }

    public void stop() {
        if (remaining > 0) {
            remaining = 0;
            title = null;
            log.add("ads: stopped");
        }
    }
}
