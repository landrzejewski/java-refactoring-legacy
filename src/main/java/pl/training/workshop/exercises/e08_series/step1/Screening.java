package pl.training.workshop.exercises.e08_series.step1;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: seans sam umie się skopiować. {@link #copy()} robi głęboką kopię mapy dopłat,
 * a {@link #withStart}, {@link #withHall} i {@link #withSurcharge} to "kopia z jedną zmianą".
 * Mapa dopłat nie jest już współdzielona: konstruktor ją kopiuje, a getter zwraca widok tylko do odczytu.
 * Konstruktor o dziewięciu parametrach i rozsiane wartości domyślne zostają do kroku 2.
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
        this.surcharges = surcharges != null ? new LinkedHashMap<>(surcharges) : new LinkedHashMap<>();
        this.subtitles = subtitles;
        this.dubbing = dubbing;
        this.ageLimit = ageLimit;
    }

    /** Pełna, niezależna kopia seansu (prototyp). */
    public Screening copy() {
        return new Screening(title, format, hall, start, basePrice, surcharges, subtitles, dubbing, ageLimit);
    }

    public Screening withStart(LocalDateTime newStart) {
        return new Screening(title, format, hall, newStart, basePrice, surcharges, subtitles, dubbing, ageLimit);
    }

    public Screening withHall(String newHall) {
        return new Screening(title, format, newHall, start, basePrice, surcharges, subtitles, dubbing, ageLimit);
    }

    public Screening withSurcharge(String name, Money amount) {
        Map<String, Money> extended = new LinkedHashMap<>(surcharges);
        extended.put(name, amount);
        return new Screening(title, format, hall, start, basePrice, extended, subtitles, dubbing, ageLimit);
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
        return Collections.unmodifiableMap(surcharges);
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
