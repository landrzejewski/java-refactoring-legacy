using System.Globalization;

namespace Training.Workshop.M3.S04DryTests.Start;

/// <summary>
/// Start: specyfikacja cen "maksymalnie DRY". Jeden helper, przypadki zaszyfrowane
/// w stringach ("3D S 11"), a oczekiwana cena liczona z tej samej taryfy i tym samym
/// wzorem co kod produkcyjny. Błąd w taryfie przechodzi niezauważony: test nie ma
/// niezależnej wyroczni. Zwraca listę niespełnionych przypadków (pusta = zielono).
/// <para>W projekcie byłaby to klasa testowa xUnit; w warsztacie leży w bibliotece,
/// żeby działał mechanizm Start/StepN.</para>
/// </summary>
public sealed class TicketPriceSpecs
{
    private static readonly IReadOnlyList<string> Specs = ["IMAX N 20", "3D S 11", "2D E 18", "2D C 10"];

    public IReadOnlyList<string> Run(TicketPrice price)
    {
        var failures = new List<string>();
        foreach (var spec in Specs)
        {
            if (!Check(price, spec))
            {
                failures.Add(spec);
            }
        }
        return failures;
    }

    private static bool Check(TicketPrice price, string spec)
    {
        var p = spec.Split(' ');
        var type = p[1] switch
        {
            "S" => "STUDENT",
            "E" => "SENIOR",
            "C" => "CHILD",
            _ => "NORMAL",
        };
        var start = new TimeOnly(int.Parse(p[2], CultureInfo.InvariantCulture), 0);
        var @base = price.Tariff.BasePrices[p[0]];
        var discount = Math.Round(@base * price.Tariff.DiscountPercents[type] / 100, 2, MidpointRounding.AwayFromZero);
        var expected = @base - discount - (start.Hour < 12 ? 5.00m : 0m);
        return price.Of(p[0], type, start) == expected;
    }
}
