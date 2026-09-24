using System.Text.RegularExpressions;

namespace Training.Workshop.M8.S07Adr;

/// <summary>
/// Wykonywalny model decyzji z ADR-0007. Każda reguła ma identyfikator z ADR, więc naruszenie
/// w teście prowadzi wprost do uzasadnienia decyzji. Reguły działają na tekście źródeł
/// (świadome uproszczenie - patrz "Konsekwencje" w ADR).
/// </summary>
public static class ArchitectureRules
{
    /// <summary>Reguła z ADR: identyfikator i zakazany wzorzec w linii kodu.</summary>
    public sealed class Rule
    {
        public static readonly Rule R1PricingWithoutNotification =
            new("ADR-0007/R1", new Regex(@"^using .*\.Notification\b"));

        public static readonly Rule R2PricingUsesMoney =
            new("ADR-0007/R2", new Regex(@"\b(double|Double)\b"));

        private Rule(string id, Regex forbidden)
        {
            Id = id;
            Forbidden = forbidden;
        }

        public static IReadOnlyList<Rule> Values { get; } = [R1PricingWithoutNotification, R2PricingUsesMoney];

        public string Id { get; }

        internal Regex Forbidden { get; }
    }

    /// <summary>Naruszenia w przestrzeni nazw Pricing wariantu, np. "ADR-0007/R2 TicketPricing.cs:12".</summary>
    public static IReadOnlyList<string> Violations(string variantDir)
    {
        var violations = new List<string>();
        foreach (string file in CSharpFiles(Path.Combine(variantDir, "Pricing")))
        {
            string[] lines = File.ReadAllLines(file);
            for (int i = 0; i < lines.Length; i++)
            {
                string line = lines[i].Trim();
                if (IsComment(line))
                {
                    continue;
                }
                foreach (Rule rule in Rule.Values)
                {
                    if (rule.Forbidden.IsMatch(line))
                    {
                        violations.Add(rule.Id + " " + Path.GetFileName(file) + ":" + (i + 1));
                    }
                }
            }
        }
        return violations;
    }

    private static bool IsComment(string line)
    {
        return line.StartsWith("//", StringComparison.Ordinal)
            || line.StartsWith("/*", StringComparison.Ordinal)
            || line.StartsWith('*');
    }

    private static IReadOnlyList<string> CSharpFiles(string dir)
    {
        return Directory.GetFiles(dir)
            .Where(f => f.EndsWith(".cs", StringComparison.Ordinal))
            .Order(StringComparer.Ordinal)
            .ToList();
    }
}
