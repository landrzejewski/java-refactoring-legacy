package pl.training.workshop.m7.s07_arrowhead;

/** Stabilny kontrakt sceny: próba rezerwacji z wynikami wcześniejszych sprawdzeń. */
public record BookingAttempt(String email, boolean screeningFound, boolean salesOpen,
        boolean customerBlocked, int requestedSeats, int freeSeats) {
}
