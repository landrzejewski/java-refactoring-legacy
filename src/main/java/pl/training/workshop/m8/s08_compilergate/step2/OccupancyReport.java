package pl.training.workshop.m8.s08_compilergate.step2;

/**
 * Krok 2: nowe API cennika - nazwa formatu liczona raz i przekazana do basePrice(String).
 * Zostaje ostrzeżenie fallthrough.
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
        return name + " [" + features + "], cena " + PriceTable.basePrice(name)
                + " zl, zajete: " + map.takenPerRow();
    }
}
