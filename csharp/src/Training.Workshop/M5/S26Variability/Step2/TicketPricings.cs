namespace Training.Workshop.M5.S26Variability.Step2;

/// <summary>
/// Krok 2: bez zmian - jedno miejsce wyboru strategii po kodzie typu biletu (N, S, E, C).
/// Strategie są bezstanowe, więc współdzielimy jedne instancje.
/// </summary>
public static class TicketPricings
{
    private static readonly IReadOnlyDictionary<string, TicketPricing> ByType = new Dictionary<string, TicketPricing>
    {
        ["N"] = formatBase => formatBase,
        ["S"] = Discounted(25),
        ["E"] = Discounted(30),
        ["C"] = Discounted(40),
    };

    public static TicketPricing ForType(string code)
    {
        if (!ByType.TryGetValue(code, out var pricing))
        {
            throw new ArgumentException("unknown ticket type: " + code);
        }
        return pricing;
    }

    private static TicketPricing Discounted(int percent)
    {
        return formatBase => formatBase.Minus(formatBase.Percent(percent));
    }
}
