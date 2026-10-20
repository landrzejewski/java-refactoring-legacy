package pl.training.workshop.m5.s29_boundaryobject.step3;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: serwis zależy tylko od portu PaymentTerminal i widzi wyłącznie PaymentConfirmation.
 * Granica jest kompletna: Adapter (kształt wywołania) + mapper (format danych)
 * + Boundary Object (poprawna wartość w języku domeny).
 */
public final class PaymentService {
    private final PaymentTerminal terminal;
    private final Map<String, PaymentConfirmation> confirmations = new LinkedHashMap<>();

    public PaymentService(PaymentTerminal terminal) {
        this.terminal = Objects.requireNonNull(terminal, "terminal");
    }

    public String pay(String bookingId, Money amount) {
        PaymentConfirmation confirmation = terminal.authorize(bookingId, amount);
        confirmations.put(bookingId, confirmation);
        if (!confirmation.approved()) {
            return confirmation.outcome() + " " + confirmation.code();
        }
        if (confirmation.isPartialFor(amount)) {
            return "PARTIAL " + confirmation.code() + " brakuje " + amount.minus(confirmation.authorized());
        }
        return "PAID " + confirmation.code();
    }

    public String receipt(String bookingId) {
        PaymentConfirmation confirmation = confirmations.get(bookingId);
        if (confirmation == null) {
            return "Brak platnosci " + bookingId;
        }
        if (!confirmation.approved()) {
            return "Platnosc " + bookingId + " nieudana: " + confirmation.code();
        }
        return "Platnosc " + bookingId + ": " + confirmation.authorized() + " (" + confirmation.code() + ")";
    }

    public Money totalAuthorized() {
        Money total = Money.ZERO;
        for (PaymentConfirmation confirmation : confirmations.values()) {
            total = total.plus(confirmation.authorized());
        }
        return total;
    }
}
