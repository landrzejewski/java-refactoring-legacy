using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Start;

/// <summary>Start: bilet na miejsce VIP (+10.00) - ta sama etykieta, ale przez StringBuilder.</summary>
public sealed class VipTicket : Ticket
{
    public VipTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    public Money Price()
    {
        return BasePrice.Plus(Money.Of("10.00"));
    }

    public string Label()
    {
        return new StringBuilder(Title).Append(": ").Append(Price()).ToString();
    }
}
