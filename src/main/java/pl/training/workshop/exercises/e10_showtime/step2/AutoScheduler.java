package pl.training.workshop.exercises.e10_showtime.step2;

import pl.training.workshop.exercises.e10_showtime.ShowRequest;

/**
 * Krok 2: automat używa tej samej fasady co panel kasjera. Świadoma zmiana zachowania: seanse
 * startowane automatycznie trafiają teraz do raportu frekwencji (dokumentuje ją E10BehaviourChangeTest).
 */
public final class AutoScheduler {
    private final Showtime showtime;

    public AutoScheduler(Showtime showtime) {
        this.showtime = showtime;
    }

    public void startShow(ShowRequest request) {
        showtime.start(request);
    }

    public void endShow(String screeningId) {
        showtime.end(screeningId);
    }
}
