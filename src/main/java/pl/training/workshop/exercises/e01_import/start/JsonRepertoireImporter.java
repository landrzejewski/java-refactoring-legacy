package pl.training.workshop.exercises.e01_import.start;

public class JsonRepertoireImporter extends RepertoireImporter {

    private final JsonLineParser parser = new JsonLineParser();

    @Override
    protected boolean isComment(String line) {
        return line.startsWith("//");
    }

    @Override
    protected LineParser getParser() {
        return parser;
    }


}
