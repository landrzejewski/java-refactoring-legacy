package pl.training.workshop.exercises.e01_import.start;

public class CsvRepertoireImporter extends RepertoireImporter {

    private final LineParser lineParser;

    public CsvRepertoireImporter(LineParser lineParser) {
        this.lineParser = lineParser;
    }

    @Override
    protected boolean isComment(String line) {
        return line.startsWith("#");
    }

    @Override
    protected LineParser getParser() {
        return lineParser;
    }


}
