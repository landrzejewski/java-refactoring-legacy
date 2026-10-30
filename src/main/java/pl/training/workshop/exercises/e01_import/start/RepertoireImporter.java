package pl.training.workshop.exercises.e01_import.start;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.training.workshop.exercises.e01_import.ScreeningEntry;
import pl.training.workshop.shared.Money;

/**
 * Start: import repertuaru z CSV ({@code tytuł;format;sala;start}, komentarze od {@code #})
 * i z JSON Lines (jeden obiekt na wiersz, komentarze od {@code //}). Obie metody powstały przez
 * kopiowanie: pętla po wierszach, pomijanie pustych i komentarzy, numerowanie wierszy do komunikatów,
 * wspólna walidacja i sortowanie są w dwóch egzemplarzach. Różni się tylko sposób rozbioru wiersza.
 * Gdy dystrybutor doda trzeci format (XML), powstanie trzecia kopia.
 */
public abstract class RepertoireImporter {

    private static final Money MORNING_DISCOUNT = Money.of("5.00");

    public List<ScreeningEntry> importFile(String fileName, List<String> lines) {
       if (!fileName.endsWith(".jsonl") &&  !fileName.endsWith(".csv")) {
           throw new IllegalArgumentException("unsupported file: " + fileName);
       }
       return importLines(lines);
    }

    public List<ScreeningEntry> importLines(List<String> lines) {
        List<ScreeningEntry> entries = new ArrayList<>();
        int number = 0;
        for (String raw : lines) {
            number++;
            String line = raw.strip();
            if (line.isEmpty() || isComment(line)) {
                continue;
            }
            var fields = getParser().parse(line, number);
            var entry = toEntry(number, fields[0].strip(), fields[1].strip(), fields[2].strip(), fields[3].strip());
            entries.add(entry);
        }
        entries.sort(Comparator.comparing(ScreeningEntry::start).thenComparing(ScreeningEntry::title));
        return List.copyOf(entries);
    }

    protected static ScreeningEntry toEntry(int number, String title, String format, String hall, String start) {
        if (title.isEmpty()) {
            throw new IllegalArgumentException("line " + number + ": empty title");
        }
        Money base = switch (format) {
            case "2D" -> Money.of("25.00");
            case "3D" -> Money.of("32.00");
            case "IMAX" -> Money.of("40.00");
            default -> throw new IllegalArgumentException("line " + number + ": unknown format: " + format);
        };
        if (hall.isEmpty()) {
            throw new IllegalArgumentException("line " + number + ": empty hall");
        }
        LocalTime time;
        try {
            time = LocalTime.parse(start);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("line " + number + ": bad start time: " + start);
        }
        Money price = time.isBefore(LocalTime.NOON) ? base.minus(MORNING_DISCOUNT) : base;
        return new ScreeningEntry(time, title, format, hall, price);
    }

    protected abstract boolean isComment(String line);

    protected abstract LineParser getParser();

}
