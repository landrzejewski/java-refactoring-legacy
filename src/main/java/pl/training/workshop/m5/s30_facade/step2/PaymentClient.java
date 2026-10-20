package pl.training.workshop.m5.s30_facade.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Podsystem: bramka płatności. Karty kończące się na 0000 są odrzucane, karta TIMEOUT
 * symuluje awarię bramki (wyjątek, bez wpisu w dzienniku).
 */
public final class PaymentClient {
    private final List<String> log = new ArrayList<>();

    public boolean charge(String card, Money amount) {
        if (card.equals("TIMEOUT")) {
            throw new IllegalStateException("gateway timeout");
        }
        boolean accepted = !card.endsWith("0000");
        log.add((accepted ? "CHARGED " : "DECLINED ") + card + " " + amount);
        return accepted;
    }

    public List<String> log() {
        return List.copyOf(log);
    }
}
