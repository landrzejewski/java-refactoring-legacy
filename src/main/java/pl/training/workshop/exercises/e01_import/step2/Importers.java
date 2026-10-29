package pl.training.workshop.exercises.e01_import.step2;

/**
 * Krok 2: jedyne miejsce, które zna rozszerzenia plików (prosta fabryka statyczna, nie wzorzec GoF).
 * Nowy format to nowy parser, nowy importer i jeden wiersz tutaj - klient i szkielet bez zmian.
 */
public final class Importers {
    private Importers() {
    }

    public static RepertoireImporter forFile(String fileName) {
        if (fileName.endsWith(".csv")) {
            return new CsvRepertoireImporter();
        } else if (fileName.endsWith(".jsonl")) {
            return new JsonLinesRepertoireImporter();
        }
        throw new IllegalArgumentException("unsupported file: " + fileName);
    }
}
