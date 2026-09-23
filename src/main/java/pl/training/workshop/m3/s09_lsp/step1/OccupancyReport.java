package pl.training.workshop.m3.s09_lsp.step1;

/** Krok 1: raport zależy od roli SeatMap ("Use Interface Where Possible"), nie od Hall. */
public final class OccupancyReport {
    public String describe(SeatMap seats) {
        return "zajete " + (seats.capacity() - seats.freeSeats()) + " z " + seats.capacity();
    }
}
