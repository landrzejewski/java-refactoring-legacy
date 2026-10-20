namespace Training.Module4.Structural.Bridge;

/// <summary>
/// Abstrakcja w moście: rodzaj wiadomości. Trzyma referencję do implementacji
/// (<see cref="IMessageSender"/>) zamiast po niej dziedziczyć, dzięki czemu
/// 2 rodzaje wiadomości i 2 kanały to 4 klasy, a nie podklasa dla każdej kombinacji.
/// </summary>
public abstract class Message
{
    protected readonly IMessageSender sender;

    protected Message(IMessageSender sender)
    {
        this.sender = sender;
    }

    public abstract void Send(string recipient, string text);
}
