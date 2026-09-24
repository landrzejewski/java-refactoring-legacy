using Training.Workshop.Shared;

namespace Training.Workshop.M5.S06ExtractInterface.Step2;

/// <summary>
/// Krok 2: klient przechodzi na rolę - jedna lista IPriceable, jedno Add(IPriceable), jedna pętla.
/// Źródłowo zgodne (Add(ticket) nadal się kompiluje), binarnie NIE: zmieniła się sygnatura Add(...) w metadanych.
/// </summary>
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
            var rate = (decimal)item.VatPercent;
            vat = vat.Plus(new Money(Math.Round(item.Price.Amount * rate / (rate + 100), 2, MidpointRounding.AwayFromZero)));
        }
        return "Razem: " + total + ", VAT: " + vat;
    }
}
