package pl.training.workshop.m6.s03_typecode.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: Replace Type Code with Class - int zamieniany na Format na granicy (odczyt CSV),
 * metody przyjmują Format. Do CSV nadal trafia ten sam int (format.code()).
 */
public final class ScreeningCsv {
    /** Wiersz "tytuł;kod" - np. "Diuna;3". */
    public String describe(String line) {
        String[] parts = line.split(";");
        String title = parts[0];
        Format format = Format.fromCode(Integer.parseInt(parts[1].trim()));
        return title + "|" + label(format) + "|" + basePrice(format)
                + "|okulary:" + (needsGlasses(format) ? "tak" : "nie")
                + "|csv=" + toCsv(title, format);
    }

    private String label(Format format) {
        return switch (format) {
            case TWO_D -> "2D";
            case THREE_D -> "3D";
            case IMAX -> "IMAX";
        };
    }

    private Money basePrice(Format format) {
        return switch (format) {
            case TWO_D -> Money.of("25.00");
            case THREE_D -> Money.of("32.00");
            case IMAX -> Money.of("40.00");
        };
    }

    private boolean needsGlasses(Format format) {
        return format == Format.THREE_D;
    }

    private String toCsv(String title, Format format) {
        return title + ";" + format.code();
    }
}
