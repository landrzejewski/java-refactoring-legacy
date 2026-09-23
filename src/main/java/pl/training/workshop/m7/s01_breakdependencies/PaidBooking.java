package pl.training.workshop.m7.s01_breakdependencies;

import java.time.LocalDateTime;

/** Stabilny kontrakt sceny: opłacona rezerwacja, której może dotyczyć przypomnienie. */
public record PaidBooking(String id, String email, String title, LocalDateTime start, boolean reminded) {
}
