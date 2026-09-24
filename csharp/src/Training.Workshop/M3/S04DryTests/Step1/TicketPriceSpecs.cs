using System.Globalization;

namespace Training.Workshop.M3.S04DryTests.Step1;

/// <summary>
/// Krok 1: DAMP zamiast szyfru - każdy przypadek ma nazwę i jawne dane wejściowe,
/// a komunikat mówi, co się nie zgadza. Czytelniej, ale oczekiwanie nadal liczy
/// <see cref="ExpectedFromTariff"/>, czyli kopia algorytmu produkcyjnego.
/// </summary>
public sealed class TicketPriceSpecs
{
    public IReadOnlyList<string> Run(TicketPrice price)
    {
        var failures = new List<string>();
        Check(price, "normalny na wieczornym IMAX", "IMAX", "NORMAL", new TimeOnly(20, 0), failures);
        Check(price, "student na porannym 3D", "3D", "STUDENT", new TimeOnly(11, 0), failures);
        Check(price, "senior na wieczornym 2D", "2D", "SENIOR", new TimeOnly(18, 0), failures);
        Check(price, "dziecko na porannym 2D", "2D", "CHILD", new TimeOnly(10, 0), failures);
        return failures;
    }

    private static void Check(TicketPrice price, string example, string format, string type, TimeOnly start,
                              List<string> failures)
    {
        var expected = ExpectedFromTariff(price.Tariff, format, type, start);
        var actual = price.Of(format, type, start);
        if (actual != expected)
        {
            failures.Add(example + ": oczekiwano " + Show(expected) + ", jest " + Show(actual));
        }
    }

    private static decimal ExpectedFromTariff(Tariff tariff, string format, string type, TimeOnly start)
    {
        var @base = tariff.BasePrices[format];
        var discount = Math.Round(@base * tariff.DiscountPercents[type] / 100, 2, MidpointRounding.AwayFromZero);
        var morning = start.Hour < 12 ? 5.00m : 0m;
        return @base - discount - morning;
    }

    private static string Show(decimal amount) => amount.ToString("0.00", CultureInfo.InvariantCulture);
}
