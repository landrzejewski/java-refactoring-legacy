using System.Diagnostics;
using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Step3;

/// <summary>
/// Krok 3: operacje jako switch expression po typach (z wzorcami pozycyjnymi). Każda operacja
/// w jednym miejscu (jak Visitor), bez ceremonii Accept/Visit i bez double dispatch. Ramię "_"
/// jest wymagane przez kompilator C# - nowy rodzaj pozycji wychodzi dopiero w runtime
/// (UnreachableException), więc to kompromis słabszy niż wyczerpujący switch w Javie 25.
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

    internal static string Line(IOrderItem item) => item switch
    {
        TicketItem(var title, var format, var price) => "Bilet " + title + " " + format + " " + price,
        SnackItem(var name, var price) => name + " " + price,
        VoucherItem(var code, var value) => "Voucher " + code + " -" + value,
        _ => throw new UnreachableException("unknown item: " + item),
    };

    internal static Money Amount(IOrderItem item) => item switch
    {
        TicketItem ticket => ticket.Price,
        SnackItem snack => snack.Price,
        VoucherItem voucher => Money.Zero.Minus(voucher.Value),
        _ => throw new UnreachableException("unknown item: " + item),
    };

    internal static Money Vat(IOrderItem item) => item switch
    {
        TicketItem ticket => VatOf(ticket.Price, 8),
        SnackItem snack => VatOf(snack.Price, 23),
        VoucherItem => Money.Zero,
        _ => throw new UnreachableException("unknown item: " + item),
    };

    /// <summary>VAT zawarty w cenie brutto: brutto * stawka / (100 + stawka).</summary>
    private static Money VatOf(Money gross, int rate)
    {
        return new Money(Math.Round(gross.Amount * rate / (100 + rate), 2, MidpointRounding.AwayFromZero));
    }
}
