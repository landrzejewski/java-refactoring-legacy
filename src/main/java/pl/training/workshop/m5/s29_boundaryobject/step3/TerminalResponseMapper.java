package pl.training.workshop.m5.s29_boundaryobject.step3;

import java.math.BigDecimal;

import pl.training.workshop.m5.s29_boundaryobject.step3.PaymentConfirmation.Outcome;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: mapper na granicy, teraz używany tylko przez adapter. Jedyne miejsce,
 * które zna format terminala: grosze w obie strony i tekst "STATUS;KOD;KWOTA".
 * Niepoprawna odpowiedź jest odrzucana tu, a nie trzy metody dalej.
 */
public final class TerminalResponseMapper {
    public long toGrosze(Money amount) {
        return amount.amount().movePointRight(2).longValueExact();
    }

    public PaymentConfirmation toConfirmation(String raw) {
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
