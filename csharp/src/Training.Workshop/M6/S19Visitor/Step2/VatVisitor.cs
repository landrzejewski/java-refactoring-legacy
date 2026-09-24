using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Step2;

/// <summary>Krok 2: operacja "VAT zawarty w cenie" jako Visitor (bilety 8%, bar 23%, voucher 0).</summary>
public sealed class VatVisitor : IOrderItemVisitor<Money>
{
    public Money VisitTicket(TicketItem ticket)
    {
        return VatOf(ticket.Price, 8);
    }

    public Money VisitSnack(SnackItem snack)
    {
        return VatOf(snack.Price, 23);
    }

    public Money VisitVoucher(VoucherItem voucher)
    {
        return Money.Zero;
    }

    /// <summary>VAT zawarty w cenie brutto: brutto * stawka / (100 + stawka).</summary>
    private static Money VatOf(Money gross, int rate)
    {
        return new Money(Math.Round(gross.Amount * rate / (100 + rate), 2, MidpointRounding.AwayFromZero));
    }
}
