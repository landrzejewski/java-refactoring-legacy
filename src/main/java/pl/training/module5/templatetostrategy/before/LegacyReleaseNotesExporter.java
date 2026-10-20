package pl.training.module5.templatetostrategy.before;

import java.util.List;

import pl.training.module5.templatetostrategy.ChangeEntry;

/**
 * Template Method z dwiema niezależnymi osiami (format i zakres treści) zaszytymi
 * w podklasach. Każda kombinacja to osobna klasa: 2 formaty x 2 zakresy = 4 podklasy,
 * a kolejny format dokłada następne dwie.
 */
public abstract class LegacyReleaseNotesExporter {
    public final String export(String version, List<ChangeEntry> entries) {
        var output = new StringBuilder(header(version));
        for (ChangeEntry entry : entries) {
            if (include(entry)) {
                output.append(formatEntry(entry));
            }
        }
        return output.append(footer()).toString();
    }

    protected abstract String header(String version);

    protected abstract boolean include(ChangeEntry entry);

    protected abstract String formatEntry(ChangeEntry entry);

    protected abstract String footer();
}
