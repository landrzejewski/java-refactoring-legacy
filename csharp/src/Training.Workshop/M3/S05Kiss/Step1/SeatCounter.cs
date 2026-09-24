using System.Text.RegularExpressions;

namespace Training.Workshop.M3.S05Kiss.Step1;

/// <summary>
/// Krok 1: Replace Parameter with Explicit Methods - zamiast stringa "all"/"vip"
/// i refleksji dwie jawne metody. Kompilator i IDE znów widzą przepływ,
/// literówka nie przejdzie kompilacji.
/// </summary>
public sealed class SeatCounter
{
    private static readonly Regex Free = new(@"(?<seat>\.)");

    public string Summary(Hall hall)
    {
        return "wolne: " + FreeSeats(hall) + ", wolne VIP: " + FreeVipSeats(hall);
    }

    private static long FreeSeats(Hall hall)
    {
        return CountFree(hall.Rows);
    }

    private static long FreeVipSeats(Hall hall)
    {
        return CountFree(Enumerable.Range(1, hall.Rows.Count)
            .Where(n => n >= hall.VipFromRow)
            .Select(n => hall.Rows[n - 1]));
    }

    private static long CountFree(IEnumerable<string> rows)
    {
        return rows.SelectMany(row => Free.Matches(row).Select(m => m.Groups["seat"].Value)).LongCount();
    }
}
