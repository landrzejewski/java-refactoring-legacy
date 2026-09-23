package pl.training.workshop.m6.s18_collectingparameter;

import java.time.LocalDateTime;
import java.util.List;

/** Stabilny kontrakt sceny: szkic rezerwacji do walidacji (now podawane jawnie - testowalność). */
public record ReservationDraft(String email, List<String> seats,
                               LocalDateTime showStart, LocalDateTime now) {
}
