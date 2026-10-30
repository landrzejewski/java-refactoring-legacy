package pl.training.workshop.exercises.e01_import.start;

public class CsvLineParser implements LineParser {

    @Override
    public String[] parse(String line, int lineNumber) {
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("line " + lineNumber + ": expected 4 fields, got " + fields.length);
        }
        return fields;
    }

}
