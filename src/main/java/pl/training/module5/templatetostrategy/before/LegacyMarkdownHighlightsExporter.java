package pl.training.module5.templatetostrategy.before;

import pl.training.module5.templatetostrategy.ChangeEntry;

public class LegacyMarkdownHighlightsExporter extends LegacyReleaseNotesExporter {
    @Override
    protected String header(String version) {
        return "# Highlights " + version + "\n";
    }

    @Override
    protected boolean include(ChangeEntry entry) {
        return entry.kind() != ChangeEntry.Kind.FIX;
    }

    @Override
    protected String formatEntry(ChangeEntry entry) {
        return "- [" + entry.kind() + "] " + entry.description() + "\n";
    }

    @Override
    protected String footer() {
        return "";
    }
}
