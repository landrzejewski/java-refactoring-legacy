package pl.training.workshop.exercises.e01_import.start

import pl.training.workshop.exercises.e01_import.ImportRequest

/**
 * Start: klient importu - nocne zadanie wczytuje repertuar z pliku dystrybutora i drukuje raport.
 * Klient sam rozpoznaje format po rozszerzeniu, żeby podpisać raport; to samo rozpoznanie
 * robi jeszcze raz importer.
 */
class RepertoireImport {
    private val importer = RepertoireImporter()

    fun load(request: ImportRequest): String {
        val label = if (request.fileName.endsWith(".csv")) {
            "CSV"
        } else if (request.fileName.endsWith(".jsonl")) {
            "JSONL"
        } else {
            throw IllegalArgumentException("unsupported file: ${request.fileName}")
        }
        val entries = importer.importFile(request.fileName, request.lines)
        val report = StringBuilder("[$label] pozycji: ${entries.size}")
        for (entry in entries) {
            report.append('\n').append(entry)
        }
        return report.toString()
    }
}
