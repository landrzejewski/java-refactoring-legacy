package pl.training.module5.templatetostrategy.before;

import pl.training.module5.templatetostrategy.ChangeEntry;

public class LegacyHtmlHighlightsExporter extends LegacyReleaseNotesExporter {
    @Override
    protected String header(String version) {
        return "<h1>Highlights " + escape(version) + "</h1>\n<ul>\n";
    }

    @Override
    protected boolean include(ChangeEntry entry) {
        return entry.kind() != ChangeEntry.Kind.FIX;
    }

    @Override
    protected String formatEntry(ChangeEntry entry) {
        return "<li>[" + entry.kind() + "] " + escape(entry.description()) + "</li>\n";
    }

    @Override
    protected String footer() {
        return "</ul>\n";
    }

    // skopiowane z LegacyHtmlChangelogExporter - poprawkę trzeba wprowadzać w dwóch miejscach
    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
