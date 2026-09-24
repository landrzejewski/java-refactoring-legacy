using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Step2;

/// <summary>
/// Krok 2: wszystkie operacje jako Visitory - żadnego is. Nowa operacja = nowa klasa
/// Visitora; nowy rodzaj pozycji = zmiana interfejsu i WSZYSTKICH Visitorów.
/// </summary>
public sealed class ReceiptPrinter
{
    private readonly ReceiptLineVisitor _lines = new();
    private readonly AmountVisitor _amounts = new();
    private readonly VatVisitor _vats = new();

    public string Print(IReadOnlyList<IOrderItem> items)
    {
        var text = new StringBuilder();
        var total = Money.Zero;
        var vat = Money.Zero;
        foreach (var item in items)
        {
            text.Append(item.Accept(_lines)).Append('\n');
            total = total.Plus(item.Accept(_amounts));
            vat = vat.Plus(item.Accept(_vats));
        }
        return text.Append("Razem: ").Append(total.Max(Money.Zero)).Append('\n')
            .Append("VAT: ").Append(vat).Append('\n').ToString();
    }
}
