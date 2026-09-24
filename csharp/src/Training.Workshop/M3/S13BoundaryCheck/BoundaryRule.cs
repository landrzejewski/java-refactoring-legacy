using System.Text.RegularExpressions;

namespace Training.Workshop.M3.S13BoundaryCheck;

/// <summary>
/// Narzędzie sceny: najprostszy test architektury bez bibliotek. Skanuje pliki .cs
/// w katalogu (np. przestrzeń nazw Domain) i zgłasza dyrektywy using zawierające zakazany fragment.
/// <para>Ograniczenia (świadome): widzi tylko dyrektywy using - pełna nazwa typu w kodzie
/// (i global using w innym pliku) przejdzie. Silniejsze bramki: NetArchTest / ArchUnitNET,
/// osobne projekty (csproj) z kontrolowanymi referencjami, analizatory Roslyn, reguły w CI.</para>
/// </summary>
public sealed partial class BoundaryRule
{
    private readonly IReadOnlyList<string> _forbiddenFragments;

    public BoundaryRule(params string[] forbiddenFragments)
    {
        _forbiddenFragments = forbiddenFragments.ToList();
    }

    /// <summary>Naruszenia w formacie "Plik.cs: używana.Przestrzeń", posortowane po pliku.</summary>
    public IReadOnlyList<string> Violations(string sourceDir)
    {
        return Directory.EnumerateFiles(sourceDir, "*.cs", SearchOption.AllDirectories)
            .Order(StringComparer.Ordinal)
            .SelectMany(path => UsingsOf(path)
                .Where(IsForbidden)
                .Select(imported => Path.GetFileName(path) + ": " + imported))
            .ToList();
    }

    private bool IsForbidden(string imported)
    {
        return _forbiddenFragments.Any(imported.Contains);
    }

    private static IReadOnlyList<string> UsingsOf(string file)
    {
        return File.ReadAllLines(file)
            .Select(line => UsingDirective().Match(line.Trim()))
            .Where(match => match.Success)
            .Select(match => match.Groups["name"].Value)
            .ToList();
    }

    /// <summary>using X.Y; / using static X.Y; / using Alias = X.Y; (bez instrukcji using var ... i using (...)).</summary>
    [GeneratedRegex(@"^(?:global\s+)?using\s+(?:static\s+)?(?:\w+\s*=\s*)?(?<name>[\w.]+)\s*;")]
    private static partial Regex UsingDirective();
}
