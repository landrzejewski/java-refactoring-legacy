namespace Training.Patterns.Behavioral.Observer;

public class Application
{
    public static void Run()
    {
        var subject = new EventsBus();
        var observer = new Logger();
        subject.AddConsumer(observer.Accept);
        subject.Publish(new ServerEvent("Started"));
    }
}
