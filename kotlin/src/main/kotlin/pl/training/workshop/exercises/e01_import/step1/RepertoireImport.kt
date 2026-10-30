package pl.training.workshop.exercises.e01_import.step1

import pl.training.workshop.exercises.e01_import.ImportRequest

/**
 * Krok 1: klient bez zmian poza wywołaniem statycznej metody importu. Rozpoznanie rozszerzenia
 * dla podpisu raportu nadal jest tutaj, drugi raz w importerze.
 */
class RepertoireImport {
    fun load(request: ImportRequest): String {
        val label = if (request.fileName.endsWith(".csv")) {
            "CSV"
        } else if (request.fileName.endsWith(".jsonl")) {
            "JSONL"
        } else {
            throw IllegalArgumentException("unsupported file: ${request.fileName}")
        }
        val entries = RepertoireImporter.importFile(request.fileName, request.lines)
        val report = StringBuilder("[$label] pozycji: ${entries.size}")
        for (entry in entries) {
            report.append('\n').append(entry)
        }
        return report.toString()
    }
}
