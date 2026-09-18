namespace Training.Patterns.Creational.Prototype;

public class Application
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Application));

    public static void Run()
    {
        var fullScreenWindow = new Window(0, 0, 800, 600);
        var dialog = new Window(200, 200, 100, 50);
        //----------------------------------------------------------
        var window = fullScreenWindow.Clone();
        log.Info("Window: " + window);
    }
}
