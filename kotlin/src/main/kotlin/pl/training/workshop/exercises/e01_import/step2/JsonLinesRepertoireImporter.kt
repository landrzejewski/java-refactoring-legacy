package pl.training.workshop.exercises.e01_import.step2

/** Krok 2: konkretny twórca - importer JSON Lines mówi tylko, jaki parser tworzy i jak się podpisuje. */
class JsonLinesRepertoireImporter : RepertoireImporter() {
    override fun label(): String = "JSONL"

    override fun createLineParser(): LineParser = JsonLineParser()
}
