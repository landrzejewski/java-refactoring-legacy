using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Start;

/// <summary>
/// Start: trzy operacje (linia paragonu, kwota, VAT), każda z łańcuchem is zakończonym
/// wyjątkiem w runtime. Nowy rodzaj pozycji kompiluje się bez błędu - i wybucha na kasie.
/// </summary>
public sealed class ReceiptPrinter
{
    public string Print(IReadOnlyList<IOrderItem> items)
    {
        var text = new StringBuilder();
        var total = Money.Zero;
        var vat = Money.Zero;
        foreach (var item in items)
        {
            text.Append(Line(item)).Append('\n');
            total = total.Plus(Amount(item));
            vat = vat.Plus(Vat(item));
        }
        return text.Append("Razem: ").Append(total.Max(Money.Zero)).Append('\n')
            .Append("VAT: ").Append(vat).Append('\n').ToString();
    }

    private static string Line(IOrderItem item)
    {
        if (item is TicketItem ticket)
        {
            return "Bilet " + ticket.Title + " " + ticket.Format + " " + ticket.Price;
        }
        else if (item is SnackItem snack)
        {
            return snack.Name + " " + snack.Price;
        }
        else if (item is VoucherItem voucher)
        {
            return "Voucher " + voucher.Code + " -" + voucher.Value;
        }
        throw new ArgumentException("unknown item: " + item);
    }

    private static Money Amount(IOrderItem item)
    {
        if (item is TicketItem ticket)
        {
            return ticket.Price;
        }
        else if (item is SnackItem snack)
        {
            return snack.Price;
        }
        else if (item is VoucherItem voucher)
        {
            return Money.Zero.Minus(voucher.Value);
        }
        throw new ArgumentException("unknown item: " + item);
    }

    private static Money Vat(IOrderItem item)
    {
        if (item is TicketItem ticket)
        {
            return VatOf(ticket.Price, 8);
        }
        else if (item is SnackItem snack)
        {
            return VatOf(snack.Price, 23);
        }
        else if (item is VoucherItem)
        {
            return Money.Zero;
        }
        throw new ArgumentException("unknown item: " + item);
    }

    /// <summary>VAT zawarty w cenie brutto: brutto * stawka / (100 + stawka).</summary>
    private static Money VatOf(Money gross, int rate)
    {
        return new Money(Math.Round(gross.Amount * rate / (100 + rate), 2, MidpointRounding.AwayFromZero));
    }
}
