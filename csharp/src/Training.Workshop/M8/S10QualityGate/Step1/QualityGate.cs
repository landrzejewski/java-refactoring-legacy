namespace Training.Workshop.M8.S10QualityGate.Step1;

/// <summary>
/// Krok 1: pierwszy wykonywalny punkt listy - skan źródeł domeny: znaczniki TODO/FIXME
/// i wydruki Console.Write/Console.Error. Tanie, deterministyczne, z plikiem i linią.
/// </summary>
public sealed class QualityGate
{
    /// <summary>Lista wyników bramki; pusta lista = bramka przepuszcza zmianę.</summary>
    public IReadOnlyList<string> Evaluate(GateInput input)
    {
        var findings = new List<string>();
        ScanSources(input, findings);
        return findings;
    }

    public bool Passes(GateInput input)
    {
        return Evaluate(input).Count == 0;
    }

    /// <summary>TODO/FIXME i wydruki na konsolę w źródłach domeny.</summary>
    private static void ScanSources(GateInput input, List<string> findings)
    {
        foreach (string file in CSharpFiles(input.Sources))
        {
            string[] lines = File.ReadAllLines(file);
            for (int i = 0; i < lines.Length; i++)
            {
                string line = lines[i];
                string where = Path.GetFileName(file) + ":" + (i + 1);
                if (line.Contains("TODO") || line.Contains("FIXME"))
                {
                    findings.Add("TODO " + where);
                }
                if (line.Contains("Console.Write") || line.Contains("Console.Error"))
                {
                    findings.Add("Console " + where);
                }
            }
        }
    }

    private static IReadOnlyList<string> CSharpFiles(string dir)
    {
        return Directory.GetFiles(dir)
            .Where(f => f.EndsWith(".cs", StringComparison.Ordinal))
            .Order(StringComparer.Ordinal)
            .ToList();
    }
}
