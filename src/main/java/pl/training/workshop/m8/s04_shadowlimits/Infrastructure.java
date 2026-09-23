package pl.training.workshop.m8.s04_shadowlimits;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * "Świat zewnętrzny" sceny: poczta, bramka płatności i baza. Każde wywołanie to prawdziwy,
 * nieodwracalny efekt - dlatego zapisujemy je w dzienniku, który sprawdzają testy.
 */
public final class Infrastructure {
    private final List<String> log = new ArrayList<>();

    public void sendMail(String to, String text) {
        log.add("MAIL " + to + ": " + text);
    }

    public void charge(String card, Money amount) {
        log.add("CHARGE " + card + ": " + amount);
    }

    public void save(String row) {
        log.add("SAVE " + row);
    }

    public List<String> log() {
        return List.copyOf(log);
    }

    public long count(String kind) {
        return log.stream().filter(entry -> entry.startsWith(kind + " ")).count();
    }
}
