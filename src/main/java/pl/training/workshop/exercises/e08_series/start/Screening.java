package pl.training.workshop.exercises.e08_series.start;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import pl.training.workshop.shared.Money;

/**
 * Start: seans z konstruktorem o dziewięciu parametrach. Wywołujący przekazuje {@code null}
 * tam, gdzie "ma być domyślnie", a klasa część domyślnych wartości uzupełnia sama
 * (pusta mapa dopłat, cena bazowa z formatu w {@link #describe()}), część zostawia planerowi.
 * Mapa dopłat jest współdzielona z tym, kto ją przekazał.
 */
public final class Screening {
    private final String title;
    private final String format;
    private final String hall;
    private final LocalDateTime start;
    private final Money basePrice;
    private final Map<String, Money> surcharges;
    private final boolean subtitles;
    private final boolean dubbing;
    private final int ageLimit;

    public Screening(String title, String format, String hall, LocalDateTime start, Money basePrice,
                     Map<String, Money> surcharges, boolean subtitles, boolean dubbing, int ageLimit) {
        this.title = title;
        this.format = format;
        this.hall = hall;
        this.start = start;
        this.basePrice = basePrice;
        this.surcharges = surcharges != null ? surcharges : new LinkedHashMap<>();
        this.subtitles = subtitles;
        this.dubbing = dubbing;
        this.ageLimit = ageLimit;
    }

    public String title() {
        return title;
    }

    public String format() {
        return format;
    }

    public String hall() {
        return hall;
    }

    public LocalDateTime start() {
        return start;
    }

    public Money basePrice() {
        return basePrice;
    }

    public Map<String, Money> surcharges() {
        return surcharges;
    }

    public boolean subtitles() {
        return subtitles;
    }

    public boolean dubbing() {
        return dubbing;
    }

    public int ageLimit() {
        return ageLimit;
    }

    public String describe() {
        Money price = basePrice != null ? basePrice : defaultPrice(format);
        String hallName = hall != null ? hall : "A";
        String language = dubbing ? "dubbing" : (subtitles ? "napisy" : "oryginal");
        return title + " " + format + " sala " + hallName + " " + start + " cena " + price
                + " doplaty " + new TreeMap<>(surcharges) + " " + language + " " + ageLimit + "+";
    }

    private static Money defaultPrice(String format) {
        return switch (format) {
            case "2D" -> Money.of("25.00");
            case "3D" -> Money.of("32.00");
            case "IMAX" -> Money.of("40.00");
            default -> throw new IllegalArgumentException("unknown format: " + format);
        };
    }
}
