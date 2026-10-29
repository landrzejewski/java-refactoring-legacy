package pl.training.workshop.exercises.e01_import.start;

import java.util.List;

import pl.training.workshop.exercises.e01_import.ImportRequest;
import pl.training.workshop.exercises.e01_import.ScreeningEntry;

/**
 * Start: klient importu - nocne zadanie wczytuje repertuar z pliku dystrybutora i drukuje raport.
 * Klient sam rozpoznaje format po rozszerzeniu, żeby podpisać raport; to samo rozpoznanie
 * robi jeszcze raz importer.
 */
public final class RepertoireImport {
    private final RepertoireImporter importer = new RepertoireImporter();

    public String load(ImportRequest request) {
        String label;
        if (request.fileName().endsWith(".csv")) {
            label = "CSV";
        } else if (request.fileName().endsWith(".jsonl")) {
            label = "JSONL";
        } else {
            throw new IllegalArgumentException("unsupported file: " + request.fileName());
        }
        List<ScreeningEntry> entries = importer.importFile(request.fileName(), request.lines());
        StringBuilder report = new StringBuilder("[" + label + "] pozycji: " + entries.size());
        for (ScreeningEntry entry : entries) {
            report.append('\n').append(entry);
        }
        return report.toString();
    }
}
