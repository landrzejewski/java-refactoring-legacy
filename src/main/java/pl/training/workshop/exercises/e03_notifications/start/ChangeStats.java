package pl.training.workshop.exercises.e03_notifications.start;

/** Start: statystyki zmian repertuaru - licznik przeniesien i odwolan wypisywany po kazdej zmianie. */
public final class ChangeStats implements ScreeningListener {
    private final Journal journal;
    private int moved;
    private int cancelled;

    public ChangeStats(Journal journal) {
        this.journal = journal;
    }

    @Override
    public void onMoved(ScreeningMoved event) {
        moved++;
        journal.note("STATS przeniesione=" + moved + " odwolane=" + cancelled);
    }

    @Override
    public void onCanceled(ScreeningCancelled event) {
        cancelled++;
        journal.note("STATS przeniesione=" + moved + " odwolane=" + cancelled);
    }

}
