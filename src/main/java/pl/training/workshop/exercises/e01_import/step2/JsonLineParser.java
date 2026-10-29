package pl.training.workshop.exercises.e01_import.step2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Krok 2: parser jednego obiektu JSON z wiersza pliku JSON Lines, komentarze od {@code //}. */
public final class JsonLineParser implements LineParser {
    private static final Pattern JSON_FIELD = Pattern.compile("\"(\\w+)\"\\s*:\\s*\"([^\"]*)\"");

    @Override
    public boolean isComment(String line) {
        return line.startsWith("//");
    }

    @Override
    public RawScreening parse(String line, int number) {
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
        return new RawScreening(fields.get("title"), fields.get("format"), fields.get("hall"), fields.get("start"));
    }
}
