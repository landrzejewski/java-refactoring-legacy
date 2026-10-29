package pl.training.workshop.exercises.e08_series.step2;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: seans powstaje tylko przez {@link Builder}. Pola wymagane to tytuł, format i start;
 * reszta ma wartości domyślne kina ustalane w jednym miejscu ({@link Builder#build()}):
 * sala A, cena bazowa z formatu, dopłata VIP, okulary dla 3D, napisy, limit wieku 12.
 * Konstruktor jest prywatny, więc nie da się już zbudować seansu z {@code null}-ami,
 * a {@link #describe()} nie musi niczego uzupełniać. Kopie z kroku 1 zostają.
 */
public final class Screening {
    private static final List<String> FORMATS = List.of("2D", "3D", "IMAX");

    private final String title;
    private final String format;
    private final String hall;
    private final LocalDateTime start;
    private final Money basePrice;
    private final Map<String, Money> surcharges;
    private final boolean subtitles;
    private final boolean dubbing;
    private final int ageLimit;

    private Screening(String title, String format, String hall, LocalDateTime start, Money basePrice,
                      Map<String, Money> surcharges, boolean subtitles, boolean dubbing, int ageLimit) {
        this.title = title;
        this.format = format;
        this.hall = hall;
        this.start = start;
        this.basePrice = basePrice;
        this.surcharges = new LinkedHashMap<>(surcharges);
        this.subtitles = subtitles;
        this.dubbing = dubbing;
        this.ageLimit = ageLimit;
    }

    public static Builder builder() {
        return new Builder();
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
        String language = dubbing ? "dubbing" : (subtitles ? "napisy" : "oryginal");
        return title + " " + format + " sala " + hall + " " + start + " cena " + basePrice
                + " doplaty " + new TreeMap<>(surcharges) + " " + language + " " + ageLimit + "+";
    }

    /** Budowniczy: wymagane tytuł, format i start; reszta domyślna. Walidacja w {@link #build()}. */
    public static final class Builder {
        private static final Money VIP = Money.of("10.00");
        private static final Money GLASSES_3D = Money.of("3.00");
        private static final String DEFAULT_HALL = "A";
        private static final int DEFAULT_AGE_LIMIT = 12;

        private String title;
        private String format;
        private String hall;
        private LocalDateTime start;
        private Money basePrice;
        private final Map<String, Money> extraSurcharges = new LinkedHashMap<>();
        private boolean dubbing;
        private Integer ageLimit;

        private Builder() {
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder hall(String hall) {
            this.hall = hall;
            return this;
        }

        public Builder start(LocalDateTime start) {
            this.start = start;
            return this;
        }

        public Builder basePrice(Money basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public Builder surcharge(String name, Money amount) {
            extraSurcharges.put(name, amount);
            return this;
        }

        public Builder dubbing(boolean dubbing) {
            this.dubbing = dubbing;
            return this;
        }

        public Builder ageLimit(int ageLimit) {
            this.ageLimit = ageLimit;
            return this;
        }

        public Screening build() {
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("title must not be blank");
            }
            if (format == null || !FORMATS.contains(format)) {
                throw new IllegalArgumentException("unknown format: " + format);
            }
            if (start == null) {
                throw new IllegalArgumentException("start is required");
            }
            Money price = basePrice != null ? basePrice : defaultPrice(format);
            if (price.compareTo(Money.ZERO) < 0) {
                throw new IllegalArgumentException("base price must not be negative");
            }
            Map<String, Money> surcharges = new LinkedHashMap<>();
            surcharges.put("VIP", VIP);
            if (format.equals("3D")) {
                surcharges.put("OKULARY", GLASSES_3D);
            }
            surcharges.putAll(extraSurcharges);
            return new Screening(title, format, hall != null ? hall : DEFAULT_HALL, start, price, surcharges,
                    !dubbing, dubbing, ageLimit != null ? ageLimit : DEFAULT_AGE_LIMIT);
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
}
