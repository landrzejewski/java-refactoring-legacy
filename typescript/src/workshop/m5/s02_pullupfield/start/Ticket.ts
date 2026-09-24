/**
 * Start: baza bez stanu. Każda podklasa trzyma miejsce na sali po swojemu:
 * inna nazwa pola, inny cykl życia (setter kontra konstruktor), inna normalizacja.
 */
export abstract class Ticket {
  abstract describe(): string;
}
