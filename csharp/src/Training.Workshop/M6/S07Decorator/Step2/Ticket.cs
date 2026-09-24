using Training.Workshop.Shared;

namespace Training.Workshop.M6.S07Decorator.Step2;

/// <summary>Krok 2: flaga _insurance usunięta z rdzenia - zajmuje się nią dekorator Insurance.</summary>
public sealed class Ticket : IPricedTicket
{
    private readonly string _title;
    private readonly string _format;
    private readonly Money _base;
    private readonly bool _vip;
    private readonly bool _glasses;

    public Ticket(TicketOrder order)
    {
        _title = order.Title;
        _format = order.Format;
        _base = order.Base;
        _vip = order.Vip;
        _glasses = order.Format == "3D" && !order.OwnGlasses;
    }

    public Money Price()
    {
        var price = _base;
        if (_vip)
        {
            price = price.Plus(Money.Of("10.00"));
        }
        if (_glasses)
        {
            price = price.Plus(Money.Of("3.00"));
        }
        return price;
    }

    public string Description()
    {
        return _title + " " + _format
            + (_vip ? " +VIP" : "")
            + (_glasses ? " +okulary 3D" : "");
    }
}
