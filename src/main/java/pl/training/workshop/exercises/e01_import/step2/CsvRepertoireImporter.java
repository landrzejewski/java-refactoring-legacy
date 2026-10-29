package pl.training.workshop.exercises.e01_import.step2;

/** Krok 2: konkretny twórca - importer CSV mówi tylko, jaki parser tworzy i jak się podpisuje. */
public final class CsvRepertoireImporter extends RepertoireImporter {
    @Override
    public String label() {
        return "CSV";
    }

    @Override
    protected LineParser createLineParser() {
        return new CsvLineParser();
    }
}
