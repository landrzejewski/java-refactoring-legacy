package pl.training.workshop.m6.s20_decisionmap.step3;

import java.time.DayOfWeek;

import pl.training.workshop.shared.Money;

/**
 * Krok 3 (ścieżka B - zmienia się zestaw formatów): format jako typ z zachowaniem.
 * Nowy format (4DX, ScreenX) to jedna stała; wyjątek od reguły dnia - nadpisanie priceOn.
 */
public enum Format {
    TWO_D("2D", "25.00"),
    THREE_D("3D", "32.00"),
    IMAX("IMAX", "40.00");

    private final String code;
    private final Money base;

    Format(String code, String base) {
        this.code = code;
        this.base = Money.of(base);
    }

    public static Format of(String code) {
        for (Format format : values()) {
            if (format.code.equals(code)) {
                return format;
            }
        }
        throw new IllegalArgumentException("unknown format: " + code);
    }

    public Money priceOn(DayOfWeek day) {
        return switch (day) {
            case TUESDAY -> base.minus(base.percent(30));
            case SATURDAY, SUNDAY -> base.plus(Money.of("2.00"));
            default -> base;
        };
    }
}
