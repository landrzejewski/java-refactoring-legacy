namespace Training.Patterns.Fn;

public class FunctionalStyleMemento
{
    internal class Originator
    {
        private string? state;

        public void SetState(string state)
        {
            this.state = state;
            Console.WriteLine("Set state: " + state);
        }

        public Func<string?> Save()
        {
            var snapshot = state;
            return () => snapshot; // closure capturing state
        }

        public void Restore(Func<string?> memento)
        {
            state = memento();
            Console.WriteLine("Restored: " + state);
        }
    }

    public static void Run()
    {
        var o = new Originator();
        o.SetState("A");
        var saved = o.Save();
        o.SetState("B");
        o.Restore(saved);
    }
}
