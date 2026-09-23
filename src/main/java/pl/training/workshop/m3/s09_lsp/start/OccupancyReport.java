package pl.training.workshop.m3.s09_lsp.start;

/** Klient odczytu: raport obłożenia - potrzebuje tylko planu miejsc. */
public final class OccupancyReport {
    public String describe(Hall hall) {
        return "zajete " + (hall.capacity() - hall.freeSeats()) + " z " + hall.capacity();
    }
}
