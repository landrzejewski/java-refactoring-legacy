using Training.Workshop.Shared;

namespace Training.Workshop.M8.S03ParallelRun.Step3;

/// <summary>
/// Krok 3: poprawka znaleziona przez raport - rabat poranny odejmujemy PO zniżce procentowej,
/// tak jak legacy. Odrzucanie nieznanego formatu zostaje: to świadoma decyzja, nie rozbieżność.
/// </summary>
public sealed class CandidatePriceCalculator
{
    private static readonly Money MorningDiscount = Money.Of("5.00");
    private static readonly Money VipSurcharge = Money.Of("10.00");
    private static readonly Money Glasses3D = Money.Of("3.00");

    public Money Price(TicketQuery query)
    {
        Money @base = BasePrice(query.Format);
        Money price = @base.Minus(@base.Percent(DiscountPercent(query.Type)));
        if (query.Start.Hour < 12)
        {
            price = price.Minus(MorningDiscount);
        }
        if (query.Row >= 10)
        {
            price = price.Plus(VipSurcharge);
        }
        if (query.Format.Equals("3D"))
        {
            price = price.Plus(Glasses3D);
        }
        return price;
    }

    private static Money BasePrice(string format) => format switch
    {
        "2D" => Money.Of("25.00"),
        "3D" => Money.Of("32.00"),
        "IMAX" => Money.Of("40.00"),
        _ => throw new ArgumentException("Nieznany format: " + format),
    };

    private static int DiscountPercent(string type) => type switch
    {
        "STUDENT" => 25,
        "SENIOR" => 30,
        "CHILD" => 40,
        _ => 0,
    };
}
