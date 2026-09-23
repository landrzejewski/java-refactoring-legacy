package pl.training.workshop.m5.s02_pullupfield.start;

/**
 * Start: baza bez stanu. Każda podklasa trzyma miejsce na sali po swojemu:
 * inna nazwa pola, inny cykl życia (setter kontra konstruktor), inna normalizacja.
 */
public abstract class Ticket {
    public abstract String describe();
}
