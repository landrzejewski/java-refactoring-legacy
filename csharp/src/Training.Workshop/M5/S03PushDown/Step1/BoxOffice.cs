using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Step1;

/// <summary>
/// Krok 1: klienci na podtyp - UpgradeToVip() wołamy na zmiennej typu StandardTicket.
/// Po tym kroku nikt nie woła metody przez typ bazowy, więc można ją przesunąć w dół.
/// </summary>
public sealed class BoxOffice
{
    public Money Sell(string kind, Money basePrice, bool vip)
    {
        if ("STUDENT" == kind)
        {
            return new StudentTicket(basePrice).Price();
        }
        var ticket = new StandardTicket(basePrice);
        if (vip)
        {
            ticket.UpgradeToVip();
        }
        return ticket.Price();
    }
}
