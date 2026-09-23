package pl.training.workshop.m8.s11_stagedrollout.step2;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.zip.CRC32;

/**
 * Krok 2: deterministyczny podział - boolean zastąpiony procentem klientów. Klient trafia do
 * koszyka 0-99 ze stabilnego skrótu (CRC32) znormalizowanego e-maila: ten sam klient zawsze
 * dostaje tę samą ścieżkę, a zwiększenie procentu nikogo nie wyrzuca z nowej ścieżki.
 */
public record RolloutPolicy(int percent, Set<String> allowList) {
    public RolloutPolicy {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Procent spoza 0-100: " + percent);
        }
        allowList = Set.copyOf(allowList);
    }

    /** Dotychczasowe ustawienia produkcyjne: 0% ruchu + testerzy. */
    public static RolloutPolicy current() {
        return new RolloutPolicy(0, Set.of("anna@kino.pl", "jan@kino.pl"));
    }

    public boolean allows(String email) {
        return allowList.contains(normalize(email)) || bucket(email) < percent;
    }

    /** Koszyk 0-99 - stabilny między restartami i maszynami (nie hashCode obiektu, nie losowanie). */
    public static int bucket(String email) {
        CRC32 crc = new CRC32();
        crc.update(normalize(email).getBytes(StandardCharsets.UTF_8));
        return (int) (crc.getValue() % 100);
    }

    private static String normalize(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
