package pl.training.workshop.m5.s29_boundaryobject.step2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import pl.training.workshop.m5.s29_boundaryobject.LegacyTerminalClient;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: serwis nie zna już formatu terminala - grosze i tekst odpowiedzi tłumaczy mapper.
 * Nadal jednak sam woła LegacyTerminalClient i składa wywołanie z mapperem; to zostanie
 * schowane za portem w kroku 3.
 */
public final class PaymentService {
    private final LegacyTerminalClient terminal;
    private final TerminalResponseMapper mapper = new TerminalResponseMapper();
    private final Map<String, PaymentConfirmation> confirmations = new LinkedHashMap<>();

    public PaymentService(LegacyTerminalClient terminal) {
        this.terminal = Objects.requireNonNull(terminal, "terminal");
    }

    public String pay(String bookingId, Money amount) {
        String raw = terminal.authorize(bookingId, mapper.toGrosze(amount));
        PaymentConfirmation confirmation = mapper.toConfirmation(raw);
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
