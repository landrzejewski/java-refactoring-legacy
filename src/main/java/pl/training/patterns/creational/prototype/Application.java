package pl.training.patterns.creational.prototype;

public class Application {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws CloneNotSupportedException {
        var fullScreenWindow = new Window(0, 0, 800, 600);
        var dialog = new Window(200, 200, 100, 50);
        //----------------------------------------------------------
        var window = fullScreenWindow.clone();
        log.info("Window: " + window);
    }
}
