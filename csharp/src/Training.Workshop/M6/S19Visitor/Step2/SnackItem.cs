using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Step2;

/// <summary>Krok 2: produkt baru (VAT 23%) z Accept.</summary>
public sealed record SnackItem(string Name, Money Price) : IOrderItem
{
    public TResult Accept<TResult>(IOrderItemVisitor<TResult> visitor)
    {
        return visitor.VisitSnack(this);
    }
}
