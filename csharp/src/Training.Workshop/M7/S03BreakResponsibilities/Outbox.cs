namespace Training.Workshop.M7.S03BreakResponsibilities;

/// <summary>Stabilny kontrakt sceny: skrzynka nadawcza - efekt uboczny, który test obserwuje.</summary>
public sealed class Outbox
{
    private readonly List<string> _sent = [];

    public void Send(string to, string text)
    {
        _sent.Add(to + ": " + text);
    }

    public IReadOnlyList<string> Sent() => _sent.ToList();
}
