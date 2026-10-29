package pl.training.workshop.exercises.e01_import.step2;

import java.util.List;

import pl.training.workshop.exercises.e01_import.ImportRequest;
import pl.training.workshop.exercises.e01_import.ScreeningEntry;

/** Krok 2: klient nie zna rozszerzeń ani formatów - pyta importer o podpis raportu. */
public final class RepertoireImport {
    public String load(ImportRequest request) {
        RepertoireImporter importer = Importers.forFile(request.fileName());
        List<ScreeningEntry> entries = importer.importLines(request.lines());
        StringBuilder report = new StringBuilder("[" + importer.label() + "] pozycji: " + entries.size());
        for (ScreeningEntry entry : entries) {
            report.append('\n').append(entry);
        }
        return report.toString();
    }
}
