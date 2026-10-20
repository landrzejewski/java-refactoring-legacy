package pl.training.module5.templatetostrategy.before;

import pl.training.module5.templatetostrategy.ChangeEntry;

public class LegacyHtmlChangelogExporter extends LegacyReleaseNotesExporter {
    @Override
    protected String header(String version) {
        return "<h1>Changelog " + escape(version) + "</h1>\n<ul>\n";
    }

    @Override
    protected boolean include(ChangeEntry entry) {
        return true;
    }

    @Override
    protected String formatEntry(ChangeEntry entry) {
        return "<li>[" + entry.kind() + "] " + escape(entry.description()) + "</li>\n";
    }

    @Override
    protected String footer() {
        return "</ul>\n";
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
