package pl.training.workshop.exercises.e01_import.start;

public class CsvRepertoireImporter extends RepertoireImporter {

    private final CsvLineParser lineParser = new CsvLineParser();

    @Override
    protected boolean isComment(String line) {
        return line.startsWith("#");
    }

    @Override
    protected LineParser getParser() {
        return lineParser;
    }


}
