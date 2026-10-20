package pl.training.workshop.m5.s29_boundaryobject.start;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import pl.training.workshop.m5.s29_boundaryobject.LegacyTerminalClient;
import pl.training.workshop.shared.Money;

/**
 * Start: surowa odpowiedź terminala "OK;AUTH-R1;4500" krąży po serwisie. Każda metoda sama ją
 * rozbiera (split, startsWith, lastIndexOf), sama zgaduje znaczenie pól i sama przelicza grosze.
 * Format obcego systemu przecieka do logiki, a błędna odpowiedź wybucha daleko od granicy.
 */
public final class PaymentService {
    private final LegacyTerminalClient terminal;
    private final Map<String, String> responses = new LinkedHashMap<>();

    public PaymentService(LegacyTerminalClient terminal) {
        this.terminal = Objects.requireNonNull(terminal, "terminal");
    }

    public String pay(String bookingId, Money amount) {
        long grosze = amount.amount().movePointRight(2).longValueExact();
        String raw = terminal.authorize(bookingId, grosze);
        responses.put(bookingId, raw);
        String[] parts = raw.split(";");
        if (!parts[0].equals("OK")) {
            return parts[0] + " " + parts[1];
        }
        long authorized = Long.parseLong(parts[2]);
        if (authorized < grosze) {
            return "PARTIAL " + parts[1] + " brakuje " + BigDecimal.valueOf(grosze - authorized).movePointLeft(2);
        }
        return "PAID " + parts[1];
    }

    public String receipt(String bookingId) {
        String raw = responses.get(bookingId);
        if (raw == null) {
            return "Brak platnosci " + bookingId;
        }
        String[] parts = raw.split(";");
        if (!raw.startsWith("OK")) {
            return "Platnosc " + bookingId + " nieudana: " + parts[1];
        }
        return "Platnosc " + bookingId + ": " + new BigDecimal(parts[2]).movePointLeft(2) + " (" + parts[1] + ")";
    }

    public Money totalAuthorized() {
        Money total = Money.ZERO;
        for (String raw : responses.values()) {
            if (raw.startsWith("OK")) {
                total = total.plus(new Money(new BigDecimal(raw.substring(raw.lastIndexOf(';') + 1)).movePointLeft(2)));
            }
        }
        return total;
    }
}
