package pl.training.patterns.structural.decorator;

public abstract class ReaderDecorator implements Reader {
    protected final Reader reader;

    @Override
    public String getText() {
        return reader.getText();
    }

    public ReaderDecorator(final Reader reader) {
        this.reader = reader;
    }
}
