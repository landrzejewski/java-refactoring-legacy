using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods.Step2;

/// <summary>Krok 2: reguła implementuje tylko metodę dla swojego biletu - typ i "most" daje kontrakt.</summary>
public sealed class StudentRule : IPriceRule<StudentTicket>
{
    public Money Apply(StudentTicket ticket)
    {
        return ticket.BasePrice.Minus(ticket.BasePrice.Percent(25));
    }
}
