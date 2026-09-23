package pl.training.workshop.m6.s05_extractfactory;

import java.time.LocalDateTime;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Stabilny kontrakt sceny: utworzona rezerwacja. Kasa płaci od razu, więc expiresAt == null.
 */
public record Reservation(String id, String channel, String email, List<String> seats,
                          Money fee, LocalDateTime expiresAt) {
}
