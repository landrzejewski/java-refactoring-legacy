using System.Globalization;

namespace Training.Workshop.M7.S14ContractChange.Step2;

/// <summary>
/// Krok 2: "przy okazji" - w jednym ruchu double -&gt; decimal. Wygląda na porządki,
/// ale zmienia DWIE rzeczy w kontrakcie: zaokrąglenie połówek (29.175 -&gt; 29.18 zamiast 29.17)
/// i format zera (0m drukuje się jako "0", a nie "0.00" - decimal niesie własną skalę). Test to wykrywa.
/// </summary>
public sealed class RefundCalculator
{
    private const decimal Fee = 3.00m;

    public string Refund(double ticketsPaid, long minutesBeforeStart)
    {
        var refund = Math.Max(
            Math.Round((decimal)ticketsPaid * Share(minutesBeforeStart) - Fee, 2, MidpointRounding.AwayFromZero),
            0m);
        return refund.ToString(CultureInfo.InvariantCulture);
    }

    private static decimal Share(long minutesBeforeStart)
    {
        if (minutesBeforeStart <= 0)
        {
            return 0m;
        }
        if (minutesBeforeStart >= 24 * 60)
        {
            return 1m;
        }
        return 0.5m;
    }
}
