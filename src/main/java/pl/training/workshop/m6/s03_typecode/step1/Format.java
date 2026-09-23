package pl.training.workshop.m6.s03_typecode.step1;

/**
 * Krok 1: nowy typ dla kodu. Enum wystarcza - zestaw formatów jest mały i zamknięty.
 * Trwały kod int jest jawnym polem, nie ordinal().
 */
public enum Format {
    TWO_D(1),
    THREE_D(2),
    IMAX(3);

    private final int code;

    Format(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static Format fromCode(int code) {
        for (Format format : values()) {
            if (format.code == code) {
                return format;
            }
        }
        throw new IllegalArgumentException("unknown format code: " + code);
    }
}
