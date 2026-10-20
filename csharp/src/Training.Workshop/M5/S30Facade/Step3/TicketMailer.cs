namespace Training.Workshop.M5.S30Facade.Step3;

/// <summary>Krok 3: podsystem ukryty (internal) - wysyłka biletów mailem.</summary>
internal sealed class TicketMailer
{
    private readonly List<string> _sent = [];

    internal void Send(string email, string text)
    {
        _sent.Add(email + ": " + text);
    }

    internal IReadOnlyList<string> Sent()
    {
        return _sent.ToList();
    }
}
