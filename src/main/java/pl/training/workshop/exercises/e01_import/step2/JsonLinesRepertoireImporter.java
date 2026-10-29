package pl.training.workshop.exercises.e01_import.step2;

/** Krok 2: konkretny twórca - importer JSON Lines mówi tylko, jaki parser tworzy i jak się podpisuje. */
public final class JsonLinesRepertoireImporter extends RepertoireImporter {
    @Override
    public String label() {
        return "JSONL";
    }

    @Override
    protected LineParser createLineParser() {
        return new JsonLineParser();
    }
}
