package pl.training.workshop.exercises.e01_import.start;

import pl.training.workshop.exercises.e01_import.ScreeningEntry;

public class CsvRepertoireImporter extends RepertoireImporter {

    private static final String FIELD_SEPARATOR = ";";
    private static final int MIN_FIELDS_LENGTH = 4;
    private static final int DEFAULT_SPLIT_LIMIT = -1;

    @Override
    protected boolean isComment(String line) {
        return line.startsWith("#");
    }

    @Override
    protected ScreeningEntry parse(String line, int number) {
        var fields = line.split(FIELD_SEPARATOR, DEFAULT_SPLIT_LIMIT);
        if (!validate(fields)) {
            throw new IllegalArgumentException("line " + number + ": expected 4 fields, got " + fields.length);
        }
        return toEntry(number, fields[0].strip(), fields[1].strip(), fields[2].strip(), fields[3].strip());
    }

    private boolean validate(String[] fields) {
        return fields.length >= MIN_FIELDS_LENGTH;
    }

}
