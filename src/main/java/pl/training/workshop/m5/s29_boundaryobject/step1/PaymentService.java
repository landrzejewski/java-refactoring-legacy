package pl.training.workshop.m5.s29_boundaryobject.step1;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import pl.training.workshop.m5.s29_boundaryobject.LegacyTerminalClient;
import pl.training.workshop.m5.s29_boundaryobject.step1.PaymentConfirmation.Outcome;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: odpowiedź jest rozbierana RAZ, zaraz po wywołaniu terminala, do PaymentConfirmation.
 * Serwis przechowuje i czyta już tylko obiekt graniczny; parsowanie jest jeszcze prywatną
 * metodą serwisu - do przeniesienia w kroku 2.
 */
public final class PaymentService {
    private final LegacyTerminalClient terminal;
    private final Map<String, PaymentConfirmation> confirmations = new LinkedHashMap<>();

    public PaymentService(LegacyTerminalClient terminal) {
        this.terminal = Objects.requireNonNull(terminal, "terminal");
    }

    public String pay(String bookingId, Money amount) {
        long grosze = amount.amount().movePointRight(2).longValueExact();
        PaymentConfirmation confirmation = parse(terminal.authorize(bookingId, grosze));
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

    private static PaymentConfirmation parse(String raw) {
        String[] parts = raw.split(";", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("malformed terminal response: " + raw);
        }
        Outcome outcome = switch (parts[0]) {
            case "OK" -> Outcome.APPROVED;
            case "DECLINED" -> Outcome.DECLINED;
            case "ERROR" -> Outcome.ERROR;
            default -> throw new IllegalArgumentException("malformed terminal response: " + raw);
        };
        return new PaymentConfirmation(outcome, parts[1], new Money(new BigDecimal(parts[2]).movePointLeft(2)));
    }
}
