using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Step2;

/// <summary>Krok 2: bilet (VAT 8%) z Accept.</summary>
public sealed record TicketItem(string Title, string Format, Money Price) : IOrderItem
{
    public TResult Accept<TResult>(IOrderItemVisitor<TResult> visitor)
    {
        return visitor.VisitTicket(this);
    }
}
