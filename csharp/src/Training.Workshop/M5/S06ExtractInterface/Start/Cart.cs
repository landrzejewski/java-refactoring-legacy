using Training.Workshop.Shared;

namespace Training.Workshop.M5.S06ExtractInterface.Start;

/// <summary>
/// Start: koszyk ma dwie listy, dwa przeciążenia Add(...) i dwie kopie liczenia VAT.
/// Każdy nowy rodzaj pozycji (np. okulary 3D) to kolejna lista i kolejna pętla.
/// </summary>
public sealed class Cart
{
    private readonly List<Ticket> _tickets = [];
    private readonly List<Snack> _snacks = [];

    public void Add(Ticket ticket)
    {
        _tickets.Add(ticket);
    }

    public void Add(Snack snack)
    {
        _snacks.Add(snack);
    }

    public string Summary()
    {
        var total = Money.Zero;
        var vat = Money.Zero;
        foreach (var ticket in _tickets)
        {
            total = total.Plus(ticket.Price);
            var rate = (decimal)ticket.VatPercent;
            vat = vat.Plus(new Money(Math.Round(ticket.Price.Amount * rate / (rate + 100), 2, MidpointRounding.AwayFromZero)));
        }
        foreach (var snack in _snacks)
        {
            total = total.Plus(snack.Price);
            var rate = (decimal)snack.VatPercent;
            vat = vat.Plus(new Money(Math.Round(snack.Price.Amount * rate / (rate + 100), 2, MidpointRounding.AwayFromZero)));
        }
        return "Razem: " + total + ", VAT: " + vat;
    }
}
