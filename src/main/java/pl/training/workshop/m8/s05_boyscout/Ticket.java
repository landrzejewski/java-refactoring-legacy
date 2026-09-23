package pl.training.workshop.m8.s05_boyscout;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Stabilny kontrakt sceny: dane biletu do wydruku (kwota jak w legacy - double).
 *
 * @param phone może być null - klient nie podał telefonu
 */
public record Ticket(String title, LocalDateTime start, List<String> seats, String email, String phone,
        double total) {
}
