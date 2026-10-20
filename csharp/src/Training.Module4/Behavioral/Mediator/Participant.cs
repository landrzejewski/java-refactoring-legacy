namespace Training.Module4.Behavioral.Mediator;

/// <summary>
/// Współpracownik (colleague): zna tylko mediatora, nie innych uczestników.
/// </summary>
public class Participant
{
    private readonly IChatMediator mediator;

    public string Name { get; }

    public Participant(string name, IChatMediator mediator)
    {
        Name = name;
        this.mediator = mediator;
        mediator.Join(this);
    }

    public void Send(string text)
    {
        Console.WriteLine(Name + " sends: " + text);
        mediator.Broadcast(this, text);
    }

    public void Receive(string from, string text)
    {
        Console.WriteLine(Name + " received from " + from + ": " + text);
    }
}
