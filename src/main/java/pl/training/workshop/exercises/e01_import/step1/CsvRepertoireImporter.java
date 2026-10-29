package pl.training.workshop.exercises.e01_import.step1;

/** Krok 1: import CSV to już tylko rozpoznanie komentarza i podział wiersza na cztery pola. */
public final class CsvRepertoireImporter extends RepertoireImporter {
    @Override
    protected boolean isComment(String line) {
        return line.startsWith("#");
    }

    @Override
    protected RawScreening parseLine(String line, int number) {
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("line " + number + ": expected 4 fields, got " + fields.length);
        }
        return new RawScreening(fields[0].strip(), fields[1].strip(), fields[2].strip(), fields[3].strip());
    }
}
