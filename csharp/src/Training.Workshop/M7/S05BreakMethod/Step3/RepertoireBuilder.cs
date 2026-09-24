using System.Globalization;
using System.Text;

namespace Training.Workshop.M7.S05BreakMethod.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Extract Method dla renderowania i Inline Variable.
/// Build() opisuje algorytm na jednym poziomie abstrakcji: sprawdź, uporządkuj, wypisz.
/// Method Object nie był potrzebny - etapy przekazują sobie po jednej wartości.
/// </summary>
public sealed class RepertoireBuilder
{
    public string Build(IReadOnlyList<Screening?> screenings)
    {
        var validated = ValidateAndCopy(screenings);
        var ordered = Order(validated);
        return Render(ordered);
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

    private static List<Screening> Order(List<Screening> screenings)
    {
        var active = new List<Screening>();
        foreach (var screening in screenings)
        {
            if (!screening.Cancelled)
            {
                active.Add(screening);
            }
        }
        active = [.. active.OrderBy(s => s.Start).ThenBy(s => s.Title, StringComparer.Ordinal)];
        return active;
    }

    private static string Render(List<Screening> screenings)
    {
        var text = new StringBuilder("REPERTUAR\n");
        foreach (var screening in screenings)
        {
            text.Append(screening.Start.ToString("HH:mm", CultureInfo.InvariantCulture)).Append(' ').Append(screening.Title)
                .Append(" (").Append(screening.Format).Append("), sala ")
                .Append(screening.Hall).Append('\n');
        }
        if (screenings.Count == 0)
        {
            text.Append("brak seansow\n");
        }
        return text.ToString();
    }
}
