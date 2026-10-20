package pl.training.module5.templatetostrategy.after;

import pl.training.module5.templatetostrategy.ChangeEntry;

public final class HtmlFormat implements NotesFormat {
    @Override
    public String header(String title, String version) {
        return "<h1>" + title + " " + escape(version) + "</h1>\n<ul>\n";
    }

    @Override
    public String entry(ChangeEntry entry) {
        return "<li>[" + entry.kind() + "] " + escape(entry.description()) + "</li>\n";
    }

    @Override
    public String footer() {
        return "</ul>\n";
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
