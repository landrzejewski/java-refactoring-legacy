namespace Training.Patterns.Fn;

public class FunctionalStyleObserver
{
    internal class EventSource
    {
        private readonly List<Action<string>> observers = [];

        public void Subscribe(Action<string> observer)
        {
            observers.Add(observer);
        }

        public void Publish(string @event)
        {
            observers.ForEach(o => o(@event));
        }
    }

    public static void Run()
    {
        var source = new EventSource();
        source.Subscribe(msg => Console.WriteLine("Observer 1 received: " + msg));
        source.Subscribe(msg => Console.WriteLine("Observer 2 received: " + msg.ToUpperInvariant()));
        source.Publish("Functional Observer pattern with lambdas!");
    }
}
