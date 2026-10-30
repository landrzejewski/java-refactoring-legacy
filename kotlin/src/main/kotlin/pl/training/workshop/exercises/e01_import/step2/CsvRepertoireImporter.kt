package pl.training.workshop.exercises.e01_import.step2

/** Krok 2: konkretny twórca - importer CSV mówi tylko, jaki parser tworzy i jak się podpisuje. */
class CsvRepertoireImporter : RepertoireImporter() {
    override fun label(): String = "CSV"

    override fun createLineParser(): LineParser = CsvLineParser()
}
