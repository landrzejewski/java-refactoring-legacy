package pl.training.workshop.m8.s11_stagedrollout.step3;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.zip.CRC32;

/**
 * Krok 3: wyłącznik awaryjny (kill switch). Ma pierwszeństwo przed wszystkim - także przed
 * listą wyjątków - bo służy do natychmiastowego wycofania bez wydania nowej wersji.
 */
public record RolloutPolicy(int percent, Set<String> allowList, boolean killSwitch) {
    public RolloutPolicy {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Procent spoza 0-100: " + percent);
        }
        allowList = Set.copyOf(allowList);
    }

    /** Dotychczasowe ustawienia produkcyjne: 0% ruchu + testerzy, wyłącznik nieaktywny. */
    public static RolloutPolicy current() {
        return new RolloutPolicy(0, Set.of("anna@kino.pl", "jan@kino.pl"), false);
    }

    public boolean allows(String email) {
        if (killSwitch) {
            return false;
        }
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
