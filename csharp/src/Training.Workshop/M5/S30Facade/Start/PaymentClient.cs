using Training.Workshop.Shared;

namespace Training.Workshop.M5.S30Facade.Start;

/// <summary>
/// Podsystem: bramka płatności. Karty kończące się na 0000 są odrzucane, karta TIMEOUT
/// symuluje awarię bramki (wyjątek, bez wpisu w dzienniku).
/// </summary>
public sealed class PaymentClient
{
    private readonly List<string> _log = [];

    public bool Charge(string card, Money amount)
    {
        if (card == "TIMEOUT")
        {
            throw new InvalidOperationException("gateway timeout");
        }
        var accepted = !card.EndsWith("0000", StringComparison.Ordinal);
        _log.Add((accepted ? "CHARGED " : "DECLINED ") + card + " " + amount);
        return accepted;
    }

    public IReadOnlyList<string> Log()
    {
        return _log.ToList();
    }
}
