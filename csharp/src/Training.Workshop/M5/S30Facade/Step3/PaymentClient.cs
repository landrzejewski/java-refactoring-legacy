using Training.Workshop.Shared;

namespace Training.Workshop.M5.S30Facade.Step3;

/// <summary>
/// Krok 3: podsystem ukryty (internal) - bramka płatności. Karty kończące się na 0000
/// są odrzucane, karta TIMEOUT symuluje awarię bramki (wyjątek, bez wpisu w dzienniku).
/// </summary>
internal sealed class PaymentClient
{
    private readonly List<string> _log = [];

    internal bool Charge(string card, Money amount)
    {
        if (card == "TIMEOUT")
        {
            throw new InvalidOperationException("gateway timeout");
        }
        var accepted = !card.EndsWith("0000", StringComparison.Ordinal);
        _log.Add((accepted ? "CHARGED " : "DECLINED ") + card + " " + amount);
        return accepted;
    }

    internal IReadOnlyList<string> Log()
    {
        return _log.ToList();
    }
}
