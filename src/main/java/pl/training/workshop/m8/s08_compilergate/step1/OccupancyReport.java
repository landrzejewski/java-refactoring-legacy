package pl.training.workshop.m8.s08_compilergate.step1;

/**
 * Krok 1 (bez zmian): raport obłożenia. Nadal woła przestarzałe PriceTable.basePrice(int) (deprecation)
 * i celowo "przelatuje" z IMAX do 3D w switch (fallthrough) - IMAX ma też dźwięk Dolby.
 */
public final class OccupancyReport {
    public String describe(SeatMap map, int format) {
        String features = "";
        switch (format) {
            case 3:
                features = features + "duzy ekran, ";
            case 2:
                features = features + "dzwiek Dolby";
                break;
            default:
                features = "standard";
        }
        String name = format == 3 ? "IMAX" : format == 2 ? "3D" : "2D";
        return name + " [" + features + "], cena " + PriceTable.basePrice(format)
                + " zl, zajete: " + map.takenPerRow();
    }
}
