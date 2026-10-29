package pl.training.workshop.exercises.e03_notifications.start;

/** Start: statystyki zmian repertuaru - licznik przeniesien i odwolan wypisywany po kazdej zmianie. */
public final class ChangeStats {
    private final Journal journal;
    private int moved;
    private int cancelled;

    public ChangeStats(Journal journal) {
        this.journal = journal;
    }

    public void moved() {
        moved++;
        journal.note("STATS przeniesione=" + moved + " odwolane=" + cancelled);
    }

    public void cancelled() {
        cancelled++;
        journal.note("STATS przeniesione=" + moved + " odwolane=" + cancelled);
    }
}
