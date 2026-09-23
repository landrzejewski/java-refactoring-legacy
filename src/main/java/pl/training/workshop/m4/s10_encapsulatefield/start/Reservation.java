package pl.training.workshop.m4.s10_encapsulatefield.start;

/**
 * Start: status rezerwacji to publiczne pole. Reguły przejść (NEW -> PAID -> USED, ...)
 * pilnuje - czasem - kod klientów. Każdy może też po prostu przypisać dowolny tekst.
 */
public final class Reservation {
    public String status = "NEW";
}
