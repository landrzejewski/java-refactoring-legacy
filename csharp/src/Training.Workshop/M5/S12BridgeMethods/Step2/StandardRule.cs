using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods.Step2;

/// <summary>Krok 2: reguła implementuje tylko metodę dla swojego biletu - typ i "most" daje kontrakt.</summary>
public sealed class StandardRule : IPriceRule<StandardTicket>
{
    public Money Apply(StandardTicket ticket)
    {
        return ticket.BasePrice;
    }
}
