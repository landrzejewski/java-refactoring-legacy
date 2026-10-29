package pl.training.workshop.exercises.e10_showtime.start;

/**
 * Start: drzwi sali. Oprócz własnej logiki (zamknięte/otwarte, przeszkoda w świetle drzwi) znają
 * oświetlenie i odtwarzacz reklam, bo po zamknięciu "wiedzą", że trzeba przyciemnić i puścić reklamy.
 * Żeby to zrobić, muszą pamiętać tytuł filmu, który nie ma nic wspólnego z drzwiami.
 */
public final class Doors {
    private final DeviceLog log;
    private Lights lights;
    private AdPlayer adPlayer;
    private boolean closed;
    private boolean blocked;
    private String pendingTitle;

    public Doors(DeviceLog log) {
        this.log = log;
    }

    public void wire(Lights lights, AdPlayer adPlayer) {
        this.lights = lights;
        this.adPlayer = adPlayer;
    }

    public void close(String title) {
        if (closed) {
            throw new IllegalStateException("doors already closed");
        }
        if (blocked) {
            log.add("doors: blocked");
            lights.set(100);
            pendingTitle = title;
            return;
        }
        closed = true;
        log.add("doors: closed");
        lights.set(30);
        adPlayer.start(2, title);
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

    /** Czujnik: przejście wolne - jeśli zamknięcie czekało, drzwi same je wznawiają. */
    public void cleared() {
        if (!blocked) {
            throw new IllegalStateException("doors are not blocked");
        }
        blocked = false;
        log.add("doors: cleared");
        if (pendingTitle != null) {
            String title = pendingTitle;
            pendingTitle = null;
            close(title);
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
