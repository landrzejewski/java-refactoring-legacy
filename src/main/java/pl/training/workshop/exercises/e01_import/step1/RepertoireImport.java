package pl.training.workshop.exercises.e01_import.step1;

import java.util.List;

import pl.training.workshop.exercises.e01_import.ImportRequest;
import pl.training.workshop.exercises.e01_import.ScreeningEntry;

/**
 * Krok 1: klient bez zmian poza wywołaniem statycznej metody importu. Rozpoznanie rozszerzenia
 * dla podpisu raportu nadal jest tutaj, drugi raz w importerze.
 */
public final class RepertoireImport {
    public String load(ImportRequest request) {
        String label;
        if (request.fileName().endsWith(".csv")) {
            label = "CSV";
        } else if (request.fileName().endsWith(".jsonl")) {
            label = "JSONL";
        } else {
            throw new IllegalArgumentException("unsupported file: " + request.fileName());
        }
        List<ScreeningEntry> entries = RepertoireImporter.importFile(request.fileName(), request.lines());
        StringBuilder report = new StringBuilder("[" + label + "] pozycji: " + entries.size());
        for (ScreeningEntry entry : entries) {
            report.append('\n').append(entry);
        }
        return report.toString();
    }
}
