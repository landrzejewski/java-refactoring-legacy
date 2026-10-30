package pl.training.workshop.exercises.e01_import.start;

import java.util.function.Supplier;

public class JsonRepertoireImporter extends RepertoireImporter {

    private final Supplier<LineParser> parserSupplier;

    public JsonRepertoireImporter(Supplier<LineParser> parserSupplier) {
        this.parserSupplier = parserSupplier;
    }

    @Override
    protected boolean isComment(String line) {
        return line.startsWith("//");
    }

    @Override
    protected LineParser getParser() {
        return parserSupplier.get() ;
    }

}
