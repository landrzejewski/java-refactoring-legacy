package pl.training.workshop.exercises.e10_showtime.step1;

/**
 * Krok 1: projektor zna tylko własny stan. {@code stop()} to zwykłe zatrzymanie z kontrolą stanu,
 * {@code cut()} to awaryjne odcięcie lampy - oba są operacjami urządzenia, żadne nie steruje salą.
 */
public final class Projector {
    private final DeviceLog log;
    private boolean playing;

    public Projector(DeviceLog log) {
        this.log = log;
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
    }

    /** Awaryjne odcięcie lampy - działa w każdym stanie. */
    public void cut() {
        playing = false;
        log.add("projector: cut");
    }

    public boolean isPlaying() {
        return playing;
    }
}
