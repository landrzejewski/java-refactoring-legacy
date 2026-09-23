package pl.training.workshop.m4.s03_magicnumbers;

/** Stabilny kontrakt sceny: bilet z legacy kodem typu ("N", "S", "E", "C") i rzędem. */
public record Ticket(String type, int row) {
}
