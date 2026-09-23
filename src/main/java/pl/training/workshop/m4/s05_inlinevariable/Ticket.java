package pl.training.workshop.m4.s05_inlinevariable;

import java.time.Instant;

/**
 * Stabilny kontrakt sceny: wystawiony bilet.
 *
 * @param holdUntil do kiedy rezerwacja czeka na płatność (15 minut od wystawienia)
 */
public record Ticket(String code, String label, Instant issuedAt, Instant holdUntil) {
}
