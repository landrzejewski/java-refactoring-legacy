using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step3;

/// <summary>Krok 3: w podklasie zostaje tylko to, co naprawdę różne - cena (+10.00 za VIP).</summary>
public sealed class VipTicket : Ticket
{
    public VipTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    public override Money Price()
    {
        return BasePrice.Plus(Money.Of("10.00"));
    }
}
