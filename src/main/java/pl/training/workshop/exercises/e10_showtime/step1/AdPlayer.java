package pl.training.workshop.exercises.e10_showtime.step1;

/** Krok 1: odtwarzacz reklam liczy reklamy i zgłasza koniec playlisty - nie wie, co się wtedy dzieje w sali. */
public final class AdPlayer {
    private final DeviceLog log;
    private final HallMediator hall;
    private int remaining;

    public AdPlayer(DeviceLog log, HallMediator hall) {
        this.log = log;
        this.hall = hall;
    }

    public void start(int ads) {
        remaining = ads;
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
        hall.adsFinished();
    }

    public void stop() {
        if (remaining > 0) {
            remaining = 0;
            log.add("ads: stopped");
        }
    }
}
