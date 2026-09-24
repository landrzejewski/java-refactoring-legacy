using System.Globalization;
using System.Text;

namespace Training.Workshop.M7.S05BreakMethod.Step1;

/// <summary>
/// Krok 1: Extract Method dla fragmentu z jednym wejściem i jednym wynikiem -
/// kontrola wejścia i kopia przeniesione dosłownie do ValidateAndCopy().
/// </summary>
public sealed class RepertoireBuilder
{
    public string Build(IReadOnlyList<Screening?> screenings)
    {
        var copy = ValidateAndCopy(screenings);
        var active = new List<Screening>();
        foreach (var screening in copy)
        {
            if (!screening.Cancelled)
            {
                active.Add(screening);
            }
        }
        active = [.. active.OrderBy(s => s.Start).ThenBy(s => s.Title, StringComparer.Ordinal)];
        var text = new StringBuilder("REPERTUAR\n");
        foreach (var screening in active)
        {
            text.Append(screening.Start.ToString("HH:mm", CultureInfo.InvariantCulture)).Append(' ').Append(screening.Title)
                .Append(" (").Append(screening.Format).Append("), sala ")
                .Append(screening.Hall).Append('\n');
        }
        if (active.Count == 0)
        {
            text.Append("brak seansow\n");
        }
        return text.ToString();
    }

    private static List<Screening> ValidateAndCopy(IReadOnlyList<Screening?> screenings)
    {
        var copy = new List<Screening>();
        foreach (var screening in screenings)
        {
            if (screening == null)
            {
                throw new ArgumentException("screening must not be null");
            }
            copy.Add(screening);
        }
        return copy;
    }
}
