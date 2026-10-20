namespace Training.Workshop.M5.S30Facade.Step2;

/// <summary>Podsystem: wysyłka biletów mailem.</summary>
public sealed class TicketMailer
{
    private readonly List<string> _sent = [];

    public void Send(string email, string text)
    {
        _sent.Add(email + ": " + text);
    }

    public IReadOnlyList<string> Sent()
    {
        return _sent.ToList();
    }
}
