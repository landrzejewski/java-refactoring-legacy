package pl.training.workshop.exercises.e01_import.step2

import pl.training.workshop.exercises.e01_import.ImportRequest

/** Krok 2: klient nie zna rozszerzeń ani formatów - pyta importer o podpis raportu. */
class RepertoireImport {
    fun load(request: ImportRequest): String {
        val importer = Importers.forFile(request.fileName)
        val entries = importer.importLines(request.lines)
        val report = StringBuilder("[${importer.label()}] pozycji: ${entries.size}")
        for (entry in entries) {
            report.append('\n').append(entry)
        }
        return report.toString()
    }
}
