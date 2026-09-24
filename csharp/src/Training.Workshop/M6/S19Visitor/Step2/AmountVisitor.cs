using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Step2;

/// <summary>Krok 2: operacja "kwota do zapłaty" jako Visitor.</summary>
public sealed class AmountVisitor : IOrderItemVisitor<Money>
{
    public Money VisitTicket(TicketItem ticket)
    {
        return ticket.Price;
    }

    public Money VisitSnack(SnackItem snack)
    {
        return snack.Price;
    }

    public Money VisitVoucher(VoucherItem voucher)
    {
        return Money.Zero.Minus(voucher.Value);
    }
}
