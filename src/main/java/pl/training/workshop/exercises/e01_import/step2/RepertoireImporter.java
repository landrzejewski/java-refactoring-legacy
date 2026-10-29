package pl.training.workshop.exercises.e01_import.step2;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pl.training.workshop.exercises.e01_import.ScreeningEntry;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: szkielet importu bez zmian, ale krok zależny od formatu to teraz osobny obiekt (parser),
 * który tworzy metoda wytwórcza {@link #createLineParser()}. Podklasa importera mówi tylko,
 * jaki parser tworzy i jak się podpisuje; wybór importera po rozszerzeniu jest w {@link Importers}.
 */
public abstract class RepertoireImporter {
    private static final Money MORNING_DISCOUNT = Money.of("5.00");

    public final List<ScreeningEntry> importLines(List<String> lines) {
        LineParser parser = createLineParser();
        List<ScreeningEntry> entries = new ArrayList<>();
        int number = 0;
        for (String raw : lines) {
            number++;
            String line = raw.strip();
            if (line.isEmpty() || parser.isComment(line)) {
                continue;
            }
            entries.add(toEntry(number, parser.parse(line, number)));
        }
        entries.sort(Comparator.comparing(ScreeningEntry::start).thenComparing(ScreeningEntry::title));
        return List.copyOf(entries);
    }

    public abstract String label();

    protected abstract LineParser createLineParser();

    private static ScreeningEntry toEntry(int number, RawScreening raw) {
        if (raw.title().isEmpty()) {
            throw new IllegalArgumentException("line " + number + ": empty title");
        }
        Money base = switch (raw.format()) {
            case "2D" -> Money.of("25.00");
            case "3D" -> Money.of("32.00");
            case "IMAX" -> Money.of("40.00");
            default -> throw new IllegalArgumentException("line " + number + ": unknown format: " + raw.format());
        };
        if (raw.hall().isEmpty()) {
            throw new IllegalArgumentException("line " + number + ": empty hall");
        }
        LocalTime time;
        try {
            time = LocalTime.parse(raw.start());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("line " + number + ": bad start time: " + raw.start());
        }
        Money price = time.isBefore(LocalTime.NOON) ? base.minus(MORNING_DISCOUNT) : base;
        return new ScreeningEntry(time, raw.title(), raw.format(), raw.hall(), price);
    }
}
