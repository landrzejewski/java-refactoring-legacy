using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods.Start;

/// <summary>Start: reguła cenowa bez wspólnego typu - rejestr znajduje ją refleksją po nazwie "Apply".</summary>
public sealed class StandardRule
{
    public Money Apply(StandardTicket ticket)
    {
        return ticket.BasePrice;
    }
}
