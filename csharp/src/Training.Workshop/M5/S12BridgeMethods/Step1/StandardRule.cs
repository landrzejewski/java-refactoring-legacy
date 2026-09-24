using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods.Step1;

/// <summary>Krok 1: implementuje IPriceRule&lt;StandardTicket&gt; - w klasie są teraz DWIE metody Apply (jedna to "most").</summary>
public sealed class StandardRule : IPriceRule<StandardTicket>
{
    public Money Apply(StandardTicket ticket)
    {
        return ticket.BasePrice;
    }

    /// <summary>Implementacja nieogólnego IPriceRule wygenerowana przez IDE (Implement missing members) - ręczny "most" z rzutowaniem.</summary>
    public Money Apply(ITicket ticket)
    {
        return Apply((StandardTicket)ticket);
    }}
