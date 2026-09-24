using System.Diagnostics;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): obsługa ChildTicket (40%). Bez tej linii kontrola wyczerpania (Roslyn)
/// zgłasza "switch nie obsługuje wariantu ChildTicket".
/// </summary>
public sealed class PriceCalculator
{
    public int DiscountPercent(Ticket ticket)
    {
        return ticket switch
        {
            StandardTicket => 0,
            StudentTicket => 25,
            SeniorTicket => 30,
            ChildTicket => 40,
            _ => throw new UnreachableException("nieobsłużony wariant biletu: " + ticket.GetType().Name),
        };
    }

    public Money Price(Ticket ticket)
    {
        return ticket.BasePrice.Minus(ticket.BasePrice.Percent(DiscountPercent(ticket)));
    }
}
