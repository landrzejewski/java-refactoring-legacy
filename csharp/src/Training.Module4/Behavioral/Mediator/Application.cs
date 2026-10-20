namespace Training.Module4.Behavioral.Mediator;

public class Application
{
    public static void Run()
    {
        var room = new ChatRoom();
        var alice = new Participant("Alice", room);
        var bob = new Participant("Bob", room);
        _ = new Participant("Carol", room);
        //------------------------------------------------
        alice.Send("Hello everyone");
        bob.Send("Hi Alice");
    }
}
