using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Step2;

/// <summary>Krok 2: voucher z Accept.</summary>
public sealed record VoucherItem(string Code, Money Value) : IOrderItem
{
    public TResult Accept<TResult>(IOrderItemVisitor<TResult> visitor)
    {
        return visitor.VisitVoucher(this);
    }
}
