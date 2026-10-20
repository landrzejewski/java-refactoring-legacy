package pl.training.module5.templatetostrategy.after;

import pl.training.module5.templatetostrategy.ChangeEntry;

public final class MarkdownFormat implements NotesFormat {
    @Override
    public String header(String title, String version) {
        return "# " + title + " " + version + "\n";
    }

    @Override
    public String entry(ChangeEntry entry) {
        return "- [" + entry.kind() + "] " + entry.description() + "\n";
    }

    @Override
    public String footer() {
        return "";
    }
}
