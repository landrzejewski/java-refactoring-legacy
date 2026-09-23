package pl.training.workshop.m3.s09_lsp.start;

/** Klient zapisu: kasa ufa kontraktowi Hall.reserve. */
public final class BoxOffice {
    public String sell(Hall hall, int seat) {
        hall.reserve(seat);
        return "sprzedano miejsce " + seat + ", wolnych: " + hall.freeSeats();
    }
}
