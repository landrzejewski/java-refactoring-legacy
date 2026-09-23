package pl.training.workshop.m3.s08_ocp.step1;

/** Krok 1: Replace Type Code with Enum - zamknięty, znany kompilatorowi zbiór formatów. */
public enum Format {
    TWO_D("2D"), THREE_D("3D"), IMAX("IMAX");

    private final String code;

    Format(String code) {
        this.code = code;
    }

    public static Format parse(String code) {
        for (Format format : values()) {
            if (format.code.equals(code)) {
                return format;
            }
        }
        throw new IllegalArgumentException("nieznany format: " + code);
    }
}
