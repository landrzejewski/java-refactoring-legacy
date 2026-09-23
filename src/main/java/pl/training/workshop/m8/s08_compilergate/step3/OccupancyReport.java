package pl.training.workshop.m8.s08_compilergate.step3;

/**
 * Krok 3: switch expression zamiast przelotu między case - intencja "IMAX ma też Dolby"
 * zapisana wprost. Zero ostrzeżeń: bramka -Xlint:all -Werror przechodzi.
 */
public final class OccupancyReport {
    public String describe(SeatMap map, int format) {
        String features = switch (format) {
            case 3 -> "duzy ekran, dzwiek Dolby";
            case 2 -> "dzwiek Dolby";
            default -> "standard";
        };
        String name = format == 3 ? "IMAX" : format == 2 ? "3D" : "2D";
        return name + " [" + features + "], cena " + PriceTable.basePrice(name)
                + " zl, zajete: " + map.takenPerRow();
    }
}
