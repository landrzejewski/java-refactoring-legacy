using Training.Workshop.Shared;

namespace Training.Workshop.M8.S06ReviewableSeries.Step1;

/// <summary>
/// Krok 1 (commit 1, refaktoryzacja): Extract Method BasePrice i DiscountPercent, Money zamiast
/// decimal. Zachowanie bez zmian - dowodem jest test równoważności, nie opis w commicie.
/// </summary>
public sealed class PriceList
{
    public Money Price(TicketQuery query)
    {
        Money @base = BasePrice(query.Format);
        Money price = @base.Minus(@base.Percent(DiscountPercent(query.Type)));
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

    private static int DiscountPercent(string type) => type switch
    {
        "STUDENT" => 25,
        "SENIOR" => 30,
        "CHILD" => 40,
        _ => 0,
    };
}
