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
public final class RepertoireImporter {
    private static final Pattern JSON_FIELD = Pattern.compile("\"(\\w+)\"\\s*:\\s*\"([^\"]*)\"");
    private static final Money MORNING_DISCOUNT = Money.of("5.00");

    public List<ScreeningEntry> importFile(String fileName, List<String> lines) {
        if (fileName.endsWith(".csv")) {
            return importCsv(lines);
        } else if (fileName.endsWith(".jsonl")) {
            return importJsonLines(lines);
        }
        throw new IllegalArgumentException("unsupported file: " + fileName);
    }

    public List<ScreeningEntry> importCsv(List<String> lines) {
        List<ScreeningEntry> entries = new ArrayList<>();
        int number = 0;
        for (String raw : lines) {
            number++;
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] fields = line.split(";", -1);
            if (fields.length != 4) {
                throw new IllegalArgumentException("line " + number + ": expected 4 fields, got " + fields.length);
            }
            entries.add(toEntry(number, fields[0].strip(), fields[1].strip(), fields[2].strip(), fields[3].strip()));
        }
        entries.sort(Comparator.comparing(ScreeningEntry::start).thenComparing(ScreeningEntry::title));
        return List.copyOf(entries);
    }

    public List<ScreeningEntry> importJsonLines(List<String> lines) {
        List<ScreeningEntry> entries = new ArrayList<>();
        int number = 0;
        for (String raw : lines) {
            number++;
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            if (!line.startsWith("{") || !line.endsWith("}")) {
                throw new IllegalArgumentException("line " + number + ": not a JSON object");
            }
            Map<String, String> fields = new LinkedHashMap<>();
            Matcher matcher = JSON_FIELD.matcher(line);
            while (matcher.find()) {
                fields.put(matcher.group(1), matcher.group(2));
            }
            for (String required : List.of("title", "format", "hall", "start")) {
                if (!fields.containsKey(required)) {
                    throw new IllegalArgumentException("line " + number + ": missing field: " + required);
                }
            }
            entries.add(toEntry(number, fields.get("title"), fields.get("format"), fields.get("hall"), fields.get("start")));
        }
        entries.sort(Comparator.comparing(ScreeningEntry::start).thenComparing(ScreeningEntry::title));
        return List.copyOf(entries);
    }

    private static ScreeningEntry toEntry(int number, String title, String format, String hall, String start) {
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
}
