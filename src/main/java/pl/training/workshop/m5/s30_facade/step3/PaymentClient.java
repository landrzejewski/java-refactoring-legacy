package pl.training.workshop.m5.s30_facade.step3;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: podsystem ukryty (package-private) - bramka płatności. Karty kończące się na 0000
 * są odrzucane, karta TIMEOUT symuluje awarię bramki (wyjątek, bez wpisu w dzienniku).
 */
final class PaymentClient {
    private final List<String> log = new ArrayList<>();

    boolean charge(String card, Money amount) {
        if (card.equals("TIMEOUT")) {
            throw new IllegalStateException("gateway timeout");
        }
        boolean accepted = !card.endsWith("0000");
        log.add((accepted ? "CHARGED " : "DECLINED ") + card + " " + amount);
        return accepted;
    }

    List<String> log() {
        return List.copyOf(log);
    }
}
