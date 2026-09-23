package pl.training.workshop.m6.s03_typecode.step3;

/** Krok 3: CSV rozmawia z mapperem, reszta kodu wyłącznie z typem Format. */
public final class ScreeningCsv {
    /** Wiersz "tytuł;kod" - np. "Diuna;3". */
    public String describe(String line) {
        String[] parts = line.split(";");
        String title = parts[0];
        Format format = FormatCodes.fromCode(Integer.parseInt(parts[1].trim()));
        return title + "|" + format.label() + "|" + format.basePrice()
                + "|okulary:" + (format.requiresGlasses() ? "tak" : "nie")
                + "|csv=" + title + ";" + FormatCodes.toCode(format);
    }
}
