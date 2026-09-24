using Training.Workshop.Shared;

namespace Training.Workshop.M5.S06ExtractInterface.Step3;

/// <summary>Krok 3: koszyk pyta pozycję o kwotę VAT zamiast liczyć ją sam.</summary>
public sealed class Cart
{
    private readonly List<IPriceable> _items = [];

    public void Add(IPriceable item)
    {
        _items.Add(item);
    }

    public string Summary()
    {
        var total = Money.Zero;
        var vat = Money.Zero;
        foreach (var item in _items)
        {
            total = total.Plus(item.Price);
            vat = vat.Plus(item.VatAmount());
        }
        return "Razem: " + total + ", VAT: " + vat;
    }
}
