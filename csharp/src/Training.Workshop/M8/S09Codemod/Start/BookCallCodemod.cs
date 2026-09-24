using System.Text.RegularExpressions;

namespace Training.Workshop.M8.S09Codemod.Start;

/// <summary>
/// Start: migracja "ręczna z grepem". Lista miejsc powstaje z wyrażenia regularnego, a przepisanie
/// to zamiana tekstu dla czterech kombinacji literałów. Regex nie zna komentarzy, wywołań
/// rozbitych na kilka linii ani typu odbiorcy (HotelService też ma Book z dwoma bool).
/// </summary>
public sealed class BookCallCodemod
{
    private static readonly Regex OldCall = new(@"\bBook\(.*,\s*(true|false)\s*,\s*\w+\s*\)");

    /// <summary>Źródła projektu (API) - wersja regexowa ich nie potrzebuje.</summary>
    public BookCallCodemod(IReadOnlyList<string> projectSources)
    {
    }

    public IReadOnlyList<int> FindLines(string source)
    {
        var lines = new List<int>();
        string[] text = source.Split('\n');
        for (int i = 0; i < text.Length; i++)
        {
            if (OldCall.IsMatch(text[i]))
            {
                lines.Add(i + 1);
            }
        }
        return lines;
    }

    public string Rewrite(string source)
    {
        return source
            .Replace(", true, true)", ", Channel.Web, Glasses.Own)")
            .Replace(", true, false)", ", Channel.Web, Glasses.Rented)")
            .Replace(", false, true)", ", Channel.BoxOffice, Glasses.Own)")
            .Replace(", false, false)", ", Channel.BoxOffice, Glasses.Rented)")
            .Replace("using Cinema;", "using Cinema;\nusing Cinema.Options;");
    }
}
