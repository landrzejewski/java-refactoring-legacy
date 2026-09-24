using System.Globalization;

namespace Training.Workshop.M3.S04DryTests.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): oczekiwana cena wpisana jawnie (policzona ręcznie z regulaminu),
/// a kopia algorytmu usunięta. Wspólny zostaje tylko helper <see cref="Check"/> - fabryka
/// przypadku i format komunikatu to dopuszczalne DRY w testach. Test znów jest
/// niezależną wyrocznią: błąd w taryfie zapala czerwone światło.
/// </summary>
public sealed class TicketPriceSpecs
{
    public IReadOnlyList<string> Run(TicketPrice price)
    {
        var failures = new List<string>();
        Check(price, "normalny na wieczornym IMAX", "IMAX", "NORMAL", new TimeOnly(20, 0), "40.00", failures);
        Check(price, "student na porannym 3D", "3D", "STUDENT", new TimeOnly(11, 0), "19.00", failures);
        Check(price, "senior na wieczornym 2D", "2D", "SENIOR", new TimeOnly(18, 0), "17.50", failures);
        Check(price, "dziecko na porannym 2D", "2D", "CHILD", new TimeOnly(10, 0), "10.00", failures);
        return failures;
    }

    private static void Check(TicketPrice price, string example, string format, string type, TimeOnly start,
                              string expected, List<string> failures)
    {
        var actual = price.Of(format, type, start);
        if (actual != decimal.Parse(expected, CultureInfo.InvariantCulture))
        {
            failures.Add(example + ": oczekiwano " + expected + ", jest "
                + actual.ToString("0.00", CultureInfo.InvariantCulture));
        }
    }
}
