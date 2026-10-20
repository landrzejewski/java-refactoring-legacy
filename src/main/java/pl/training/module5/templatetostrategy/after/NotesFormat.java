package pl.training.module5.templatetostrategy.after;

import pl.training.module5.templatetostrategy.ChangeEntry;

/**
 * Strategia formatu - dawne kroki {@code header/formatEntry/footer} szablonu.
 */
public interface NotesFormat {
    String header(String title, String version);

    String entry(ChangeEntry entry);

    String footer();
}
