package pl.training.workshop.exercises.e10_showtime.step2;

/**
 * Krok 2: drzwi znają tylko własny stan (zamknięte, przeszkoda) i sterownik sali. Nie pamiętają już
 * tytułu filmu ani tego, co ma się stać po zamknięciu - tylko zgłaszają zdarzenie.
 */
final class Doors {
    private final DeviceLog log;
    private final HallMediator hall;
    private boolean closed;
    private boolean blocked;

    public Doors(DeviceLog log, HallMediator hall) {
        this.log = log;
        this.hall = hall;
    }

    public void close() {
        if (closed) {
            throw new IllegalStateException("doors already closed");
        }
        if (blocked) {
            log.add("doors: blocked");
            hall.doorsBlocked();
            return;
        }
        closed = true;
        log.add("doors: closed");
        hall.doorsClosed();
    }

    public void open() {
        closed = false;
        log.add("doors: open");
    }

    /** Czujnik: coś stoi w świetle drzwi. */
    public void obstacle() {
        blocked = true;
        log.add("doors: obstacle");
    }

    /** Czujnik: przejście wolne. */
    public void cleared() {
        if (!blocked) {
            throw new IllegalStateException("doors are not blocked");
        }
        blocked = false;
        log.add("doors: cleared");
        hall.doorsCleared();
    }

    public boolean isClosed() {
        return closed;
    }
}
