package pl.training.workshop.exercises.e01_import.step2;

/** Krok 2: parser wiersza CSV {@code tytuł;format;sala;start}, komentarze od {@code #}. */
public final class CsvLineParser implements LineParser {
    @Override
    public boolean isComment(String line) {
        return line.startsWith("#");
    }

    @Override
    public RawScreening parse(String line, int number) {
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("line " + number + ": expected 4 fields, got " + fields.length);
        }
        return new RawScreening(fields[0].strip(), fields[1].strip(), fields[2].strip(), fields[3].strip());
    }
}
