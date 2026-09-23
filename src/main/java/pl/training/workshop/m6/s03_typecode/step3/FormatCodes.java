package pl.training.workshop.m6.s03_typecode.step3;

import java.util.EnumMap;
import java.util.Map;

/**
 * Krok 3: mapper migracyjny int &lt;-&gt; Format. Baza i CSV zostają przy kodzie int;
 * gdy kiedyś przejdziemy na kody tekstowe, zmieni się tylko ta klasa.
 */
public final class FormatCodes {
    private static final Map<Format, Integer> CODES = new EnumMap<>(Map.of(
            Format.TWO_D, 1,
            Format.THREE_D, 2,
            Format.IMAX, 3));

    private FormatCodes() {
    }

    public static Format fromCode(int code) {
        for (Map.Entry<Format, Integer> entry : CODES.entrySet()) {
            if (entry.getValue() == code) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("unknown format code: " + code);
    }

    public static int toCode(Format format) {
        return CODES.get(format);
    }
}
