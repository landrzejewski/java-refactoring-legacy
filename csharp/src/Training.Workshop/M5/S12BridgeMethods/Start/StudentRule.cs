using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods.Start;

/// <summary>Start: reguła studencka (-25%) - też bez wspólnego typu.</summary>
public sealed class StudentRule
{
    public Money Apply(StudentTicket ticket)
    {
        return ticket.BasePrice.Minus(ticket.BasePrice.Percent(25));
    }
}
