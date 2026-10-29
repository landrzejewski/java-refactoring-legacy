package pl.training.workshop.exercises.e03_notifications.step1;

/** Krok 1: statystyki jako odbiorca zdarzen. */
public final class ChangeStats implements ScreeningListener {
    private final Journal journal;
    private int moved;
    private int cancelled;

    public ChangeStats(Journal journal) {
        this.journal = journal;
    }

    @Override
    public void screeningMoved(ScreeningMoved event) {
        moved++;
        journal.note("STATS przeniesione=" + moved + " odwolane=" + cancelled);
    }

    @Override
    public void screeningCancelled(ScreeningCancelled event) {
        cancelled++;
        journal.note("STATS przeniesione=" + moved + " odwolane=" + cancelled);
    }
}
