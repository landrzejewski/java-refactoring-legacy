package pl.training.workshop.exercises.e01_import.step2

/**
 * Krok 2: jedyne miejsce, które zna rozszerzenia plików (prosta fabryka statyczna, nie wzorzec GoF).
 * Nowy format to nowy parser, nowy importer i jeden wiersz tutaj - klient i szkielet bez zmian.
 */
object Importers {
    fun forFile(fileName: String): RepertoireImporter {
        if (fileName.endsWith(".csv")) {
            return CsvRepertoireImporter()
        } else if (fileName.endsWith(".jsonl")) {
            return JsonLinesRepertoireImporter()
        }
        throw IllegalArgumentException("unsupported file: $fileName")
    }
}
