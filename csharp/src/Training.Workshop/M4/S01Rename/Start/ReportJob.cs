using System.Reflection;

namespace Training.Workshop.M4.S01Rename.Start;

/// <summary>
/// Nocne zadanie raportu. Metodę wybiera konfiguracja, a nie kod - IDE nie widzi tego użycia.
/// W produkcji Config to plik report.properties na serwerze, poza repozytorium.
/// </summary>
public sealed class ReportJob
{
    internal const string Config = """
        report.method=Calc2
        report.onlineOnly=true
        """;

    private readonly SalesReport _report = new();

    public string Run(IReadOnlyList<Sale> sales)
    {
        var config = Config.Split('\n', StringSplitOptions.RemoveEmptyEntries)
            .Select(line => line.Split('=', 2))
            .ToDictionary(pair => pair[0].Trim(), pair => pair[1].Trim());
        try
        {
            var name = config["report.method"];
            var method = typeof(SalesReport).GetMethod(name, [typeof(IReadOnlyList<Sale>), typeof(bool)])
                ?? throw new MissingMethodException(nameof(SalesReport), name);
            var onlineOnly = bool.Parse(config["report.onlineOnly"]);
            return (string)method.Invoke(_report, [sales, onlineOnly])!;
        }
        catch (Exception e) when (e is MissingMethodException or TargetInvocationException)
        {
            throw new InvalidOperationException("Zadanie raportu nie działa: " + e.Message, e);
        }
    }
}
