using Training.Workshop.Shared;

namespace Training.Workshop.M5.S09Overloading.Start;

/// <summary>Start: klient po migracji na typ bazowy. Kompiluje się bez ostrzeżeń - i liczy źle.</summary>
public sealed class Checkout
{
    private readonly PriceList _priceList = new();

    public Money Total(IReadOnlyList<Ticket> tickets)
    {
        var total = Money.Zero;
        foreach (var ticket in tickets)
        {
            total = total.Plus(_priceList.Price(ticket));
        }
        return total;
    }

    public bool AlreadyInCart(IReadOnlyList<Ticket> cart, Ticket ticket)
    {
        return cart.Contains(ticket);
    }
}
