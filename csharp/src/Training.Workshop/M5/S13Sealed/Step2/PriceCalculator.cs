using System.Diagnostics;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step2;

/// <summary>
/// Krok 2: Replace Conditional with Pattern Matching - switch po wszystkich wariantach, każdy wymieniony
/// jawnie, także StandardTicket (0%). Kompilator C# nie zna zamkniętych hierarchii (bez ramienia <c>_</c>
/// zgłasza CS8509), więc ostatnie ramię to jawny odpowiednik Javowego MatchException, a wyczerpanie
/// sprawdza test oparty na Roslyn (S13SolutionTest).
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
            _ => throw new UnreachableException("nieobsłużony wariant biletu: " + ticket.GetType().Name),
        };
    }

    public Money Price(Ticket ticket)
    {
        return ticket.BasePrice.Minus(ticket.BasePrice.Percent(DiscountPercent(ticket)));
    }
}
