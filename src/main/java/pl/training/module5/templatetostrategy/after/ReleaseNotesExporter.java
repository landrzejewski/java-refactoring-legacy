package pl.training.module5.templatetostrategy.after;

import java.util.List;
import java.util.Objects;

import pl.training.module5.templatetostrategy.ChangeEntry;

/**
 * Replace Inheritance with Delegation (Template Method -> Strategy): szkielet algorytmu
 * zostaje w jednej finalnej klasie, a zmienne kroki dostarczają dwie niezależne
 * strategie. N formatów i M zakresów to N + M klas zamiast N x M podklas.
 */
public final class ReleaseNotesExporter {
    private final NotesFormat format;
    private final ContentSelection selection;

    public ReleaseNotesExporter(NotesFormat format, ContentSelection selection) {
        this.format = Objects.requireNonNull(format, "format");
        this.selection = Objects.requireNonNull(selection, "selection");
    }

    public String export(String version, List<ChangeEntry> entries) {
        var output = new StringBuilder(format.header(selection.title(), version));
        for (ChangeEntry entry : entries) {
            if (selection.includes(entry)) {
                output.append(format.entry(entry));
            }
        }
        return output.append(format.footer()).toString();
    }
}
