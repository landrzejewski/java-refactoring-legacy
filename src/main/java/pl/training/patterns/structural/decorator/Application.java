package pl.training.patterns.structural.decorator;

public class Application {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Application.class.getName());

    public static void main(String[] args) {
        Reader reader = new LowerCaseReaderDecorator(new UnderscoreReaderDecorator(new SystemInReader()));
        //------------------------------------------------
        log.info(reader.getText());
        ((ReaderDecorator) reader).getInt();
    }
}
