package pl.training.workshop.m6.s03_typecode.start;

import pl.training.workshop.shared.Money;

/**
 * Start: format seansu jako surowy int (1=2D, 2=3D, 3=IMAX). Wiedza o kodzie rozproszona
 * w trzech metodach, a needsGlasses w ogóle nie waliduje kodu. Plik CSV przechowuje int.
 */
public final class ScreeningCsv {
    public static final int FORMAT_2D = 1;
    public static final int FORMAT_3D = 2;
    public static final int FORMAT_IMAX = 3;

    /** Wiersz "tytuł;kod" - np. "Diuna;3". */
    public String describe(String line) {
        String[] parts = line.split(";");
        String title = parts[0];
        int formatCode = Integer.parseInt(parts[1].trim());
        return title + "|" + label(formatCode) + "|" + basePrice(formatCode)
                + "|okulary:" + (needsGlasses(formatCode) ? "tak" : "nie")
                + "|csv=" + toCsv(title, formatCode);
    }

    private String label(int formatCode) {
        return switch (formatCode) {
            case FORMAT_2D -> "2D";
            case FORMAT_3D -> "3D";
            case FORMAT_IMAX -> "IMAX";
            default -> throw new IllegalArgumentException("unknown format code: " + formatCode);
        };
    }

    private Money basePrice(int formatCode) {
        return switch (formatCode) {
            case FORMAT_2D -> Money.of("25.00");
            case FORMAT_3D -> Money.of("32.00");
            case FORMAT_IMAX -> Money.of("40.00");
            default -> throw new IllegalArgumentException("unknown format code: " + formatCode);
        };
    }

    private boolean needsGlasses(int formatCode) {
        return formatCode == FORMAT_3D;
    }

    private String toCsv(String title, int formatCode) {
        return title + ";" + formatCode;
    }
}
