package pl.training.workshop.m8.s08_compilergate.step3;

/** Krok 3 (bez zmian): stare API (przestarzałe) i nowe API cennika. */
public final class PriceTable {
    private PriceTable() {
    }

    /**
     * Cena bazowa wg kodu z CinemaManager.
     *
     * @deprecated kody int (1 = 2D, 2 = 3D, 3 = IMAX) - użyj {@link #basePrice(String)}
     */
    @Deprecated
    public static int basePrice(int format) {
        return format == 3 ? 40 : format == 2 ? 32 : 25;
    }

    public static int basePrice(String format) {
        return switch (format) {
            case "IMAX" -> 40;
            case "3D" -> 32;
            default -> 25;
        };
    }
}
