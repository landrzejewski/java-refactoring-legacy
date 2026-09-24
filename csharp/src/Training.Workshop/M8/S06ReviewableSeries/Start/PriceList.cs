using Training.Workshop.Shared;

namespace Training.Workshop.M8.S06ReviewableSeries.Start;

/// <summary>
/// Start: cennik, do którego trzeba dodać promocję "Tani wtorek" (NORMAL -20% we wtorek).
/// Zniżki są wplecione w jedną metodę i zależą tylko od typu biletu - nowa reguła
/// potrzebuje daty. Pokusa: jeden duży commit "porządki + tani wtorek".
/// </summary>
public sealed class PriceList
{
    public Money Price(TicketQuery q)
    {
        decimal p;
        if (q.Format.Equals("IMAX"))
        {
            p = 40.00m;
        }
        else if (q.Format.Equals("3D"))
        {
            p = 32.00m;
        }
        else
        {
            p = 25.00m;
        }
        decimal d;
        if (q.Type.Equals("STUDENT"))
        {
            d = p * 0.25m;
        }
        else if (q.Type.Equals("SENIOR"))
        {
            d = p * 0.30m;
        }
        else if (q.Type.Equals("CHILD"))
        {
            d = p * 0.40m;
        }
        else
        {
            d = 0m;
        }
        p = p - d;
        if (q.Start.Hour < 12)
        {
            p = p - 5.00m;
        }
        if (q.Row >= 10)
        {
            p = p + 10.00m;
        }
        return new Money(p);
    }
}
