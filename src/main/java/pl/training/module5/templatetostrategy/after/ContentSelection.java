package pl.training.module5.templatetostrategy.after;

import java.util.Objects;
import java.util.function.Predicate;

import pl.training.module5.templatetostrategy.ChangeEntry;

/**
 * Strategia zakresu treści - dawny krok {@code include} szablonu razem z tytułem.
 */
public record ContentSelection(String title, Predicate<ChangeEntry> filter) {
    public static final ContentSelection CHANGELOG =
            new ContentSelection("Changelog", entry -> true);
    public static final ContentSelection HIGHLIGHTS =
            new ContentSelection("Highlights", entry -> entry.kind() != ChangeEntry.Kind.FIX);

    public ContentSelection {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        Objects.requireNonNull(filter, "filter");
    }

    public boolean includes(ChangeEntry entry) {
        return filter.test(entry);
    }
}
