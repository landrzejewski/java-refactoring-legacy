using Training.Workshop.Shared;

namespace Training.Workshop.M6.S07Decorator.Start;

/// <summary>
/// Start: rdzeń biletu obrośnięty flagami dodatków. Każdy nowy dodatek to kolejne pole,
/// kolejny if w Price() i Description() - a większość biletów nie ma żadnego dodatku.
/// </summary>
public sealed class Ticket
{
    private readonly string _title;
    private readonly string _format;
    private readonly Money _base;
    private readonly bool _vip;
    private readonly bool _glasses;
    private readonly bool _insurance;

    public Ticket(TicketOrder order)
    {
        _title = order.Title;
        _format = order.Format;
        _base = order.Base;
        _vip = order.Vip;
        _glasses = order.Format == "3D" && !order.OwnGlasses;
        _insurance = order.Insurance;
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
        if (_insurance)
        {
            price = price.Plus(Money.Of("4.00"));
        }
        return price;
    }

    public string Description()
    {
        return _title + " " + _format
            + (_vip ? " +VIP" : "")
            + (_glasses ? " +okulary 3D" : "")
            + (_insurance ? " +ubezpieczenie" : "");
    }
}
