package pl.training.workshop.exercises.e10_showtime.start;

/**
 * Start: projektor. {@code stop()} kończy seans i "przy okazji" przywraca dźwięk, światła i drzwi,
 * więc zna trzy inne urządzenia. {@code cut()} powstał dla procedury awaryjnej, bo tam kaskada
 * po {@code stop()} dublowała wpisy w dzienniku - dwa sposoby wyłączenia jednego urządzenia.
 */
public final class Projector {
    private final DeviceLog log;
    private Sound sound;
    private Lights lights;
    private Doors doors;
    private boolean playing;

    public Projector(DeviceLog log) {
        this.log = log;
    }

    public void wire(Sound sound, Lights lights, Doors doors) {
        this.sound = sound;
        this.lights = lights;
        this.doors = doors;
    }

    public void play(String title) {
        if (playing) {
            throw new IllegalStateException("projector already playing");
        }
        playing = true;
        log.add("projector: play " + title);
    }

    public void stop() {
        if (!playing) {
            throw new IllegalStateException("projector is not playing");
        }
        playing = false;
        log.add("projector: stop");
        sound.set(0);
        lights.set(100);
        doors.open();
    }

    /** Awaryjne odcięcie lampy - bez kaskady. */
    public void cut() {
        playing = false;
        log.add("projector: cut");
    }

    public boolean isPlaying() {
        return playing;
    }
}
