package pl.training.workshop.exercises.e01_import.start;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonLineParser implements LineParser {

    private static final Pattern JSON_FIELD = Pattern.compile("\"(\\w+)\"\\s*:\\s*\"([^\"]*)\"");

    @Override
    public String[] parse(String line, int lineNumber) {
        if (!line.startsWith("{") || !line.endsWith("}")) {
            throw new IllegalArgumentException("line " + lineNumber + ": not a JSON object");
        }
        Map<String, String> fields = new LinkedHashMap<>();
        Matcher matcher = JSON_FIELD.matcher(line);
        while (matcher.find()) {
            fields.put(matcher.group(1), matcher.group(2));
        }
        for (String required : List.of("title", "format", "hall", "start")) {
            if (!fields.containsKey(required)) {
                throw new IllegalArgumentException("line " + lineNumber + ": missing field: " + required);
            }
        }
        return new String[]{fields.get("title"), fields.get("format"), fields.get("hall"), fields.get("start")};
    }

}
