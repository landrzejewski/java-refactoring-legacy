package pl.training.workshop.m6.s03_typecode.step2;

/** Krok 2: klient pyta obiekt formatu zamiast wykonywać switch. */
public final class ScreeningCsv {
    /** Wiersz "tytuł;kod" - np. "Diuna;3". */
    public String describe(String line) {
        String[] parts = line.split(";");
        String title = parts[0];
        Format format = Format.fromCode(Integer.parseInt(parts[1].trim()));
        return title + "|" + format.label() + "|" + format.basePrice()
                + "|okulary:" + (format.requiresGlasses() ? "tak" : "nie")
                + "|csv=" + title + ";" + format.code();
    }
}
