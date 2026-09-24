using Training.Workshop.Shared;

namespace Training.Workshop.M8.S06ReviewableSeries.Step3;

/// <summary>
/// Krok 3 (commit 3, zmiana zachowania): nowa reguła "Tani wtorek" - NORMAL -20% ceny bazowej
/// we wtorek. Diff to kilka linii, więc recenzent widzi wyłącznie nową regułę biznesową.
/// </summary>
public sealed class PriceList
{
    private const int CheapTuesdayPercent = 20;

    public Money Price(TicketQuery query)
    {
        Money @base = BasePrice(query.Format);
        Money price = @base.Minus(@base.Percent(DiscountPercent(query)));
        if (query.Start.Hour < 12)
        {
            price = price.Minus(Money.Of("5.00"));
        }
        if (query.Row >= 10)
        {
            price = price.Plus(Money.Of("10.00"));
        }
        return price;
    }

    private static Money BasePrice(string format) => format switch
    {
        "IMAX" => Money.Of("40.00"),
        "3D" => Money.Of("32.00"),
        _ => Money.Of("25.00"),
    };

    private static int DiscountPercent(TicketQuery query) => query.Type switch
    {
        "STUDENT" => 25,
        "SENIOR" => 30,
        "CHILD" => 40,
        _ => IsCheapTuesday(query) ? CheapTuesdayPercent : 0,
    };

    private static bool IsCheapTuesday(TicketQuery query)
    {
        return query.Start.DayOfWeek == DayOfWeek.Tuesday;
    }
}
