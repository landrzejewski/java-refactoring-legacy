using Training.Workshop.Shared;

namespace Training.Workshop.M8.S04ShadowLimits;

/// <summary>
/// "Świat zewnętrzny" sceny: poczta, bramka płatności i baza. Każde wywołanie to prawdziwy,
/// nieodwracalny efekt - dlatego zapisujemy je w dzienniku, który sprawdzają testy.
/// </summary>
public sealed class Infrastructure
{
    private readonly List<string> _log = [];

    public void SendMail(string to, string text)
    {
        _log.Add("MAIL " + to + ": " + text);
    }

    public void Charge(string card, Money amount)
    {
        _log.Add("CHARGE " + card + ": " + amount);
    }

    public void Save(string row)
    {
        _log.Add("SAVE " + row);
    }

    public IReadOnlyList<string> Log()
    {
        return _log.ToList();
    }

    public long Count(string kind)
    {
        return _log.Count(entry => entry.StartsWith(kind + " ", StringComparison.Ordinal));
    }
}
