using System.Diagnostics.CodeAnalysis;
using System.Reflection;
using System.Text.RegularExpressions;

namespace Training.Workshop.M3.S05Kiss.Start;

/// <summary>
/// Start: złożoność wprowadzona. Rodzaj miejsc wybierany stringiem i refleksją
/// ("all" -&gt; AllRows, "vip" -&gt; VipRows), wolne miejsca liczone wyrażeniem regularnym
/// z nazwaną grupą. Działa, ale IDE widzi metody wierszy jako nieużywane,
/// literówka w "vip" wybucha dopiero w runtime, a przepływ jest ukryty.
/// </summary>
public sealed class SeatCounter
{
    private static readonly Regex Free = new(@"(?<seat>\.)");

    public string Summary(Hall hall)
    {
        return "wolne: " + FreeSeats(hall, "all") + ", wolne VIP: " + FreeSeats(hall, "vip");
    }

    internal long FreeSeats(Hall hall, string kind)
    {
        var name = char.ToUpperInvariant(kind[0]) + kind[1..] + "Rows";
        var rows = GetType().GetMethod(name, BindingFlags.NonPublic | BindingFlags.Instance)
            ?? throw new InvalidOperationException("nieznany rodzaj miejsc: " + kind);
        var selected = (IEnumerable<string>)rows.Invoke(this, [hall])!;
        return selected.SelectMany(row => Free.Matches(row).Select(m => m.Groups["seat"].Value)).LongCount();
    }

    [SuppressMessage("CodeQuality", "IDE0051:Remove unused private members", Justification = "wołane refleksją")]
    private IEnumerable<string> AllRows(Hall hall)
    {
        return hall.Rows;
    }

    [SuppressMessage("CodeQuality", "IDE0051:Remove unused private members", Justification = "wołane refleksją")]
    private IEnumerable<string> VipRows(Hall hall)
    {
        return Enumerable.Range(1, hall.Rows.Count)
            .Where(n => n >= hall.VipFromRow)
            .Select(n => hall.Rows[n - 1]);
    }
}
