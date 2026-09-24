using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods.Step1;

/// <summary>Krok 1: implementuje IPriceRule&lt;StudentTicket&gt; - w klasie są teraz DWIE metody Apply (jedna to "most").</summary>
public sealed class StudentRule : IPriceRule<StudentTicket>
{
    public Money Apply(StudentTicket ticket)
    {
        return ticket.BasePrice.Minus(ticket.BasePrice.Percent(25));
    }

    /// <summary>Implementacja nieogólnego IPriceRule wygenerowana przez IDE (Implement missing members) - ręczny "most" z rzutowaniem.</summary>
    public Money Apply(ITicket ticket)
    {
        return Apply((StudentTicket)ticket);
    }}
