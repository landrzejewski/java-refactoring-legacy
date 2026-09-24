using System.Runtime.CompilerServices;

namespace Training.Workshop.Tests.M5.Tooling;

/// <summary>
/// Źródła scen modułu 5 (odpowiednik Path.of("src/main/java/pl/training/workshop/m5/...") w Javie).
/// Katalog repozytorium wyznaczany jest ze ścieżki tego pliku zapisanej przez kompilator
/// (<c>CallerFilePath</c>), więc działa niezależnie od katalogu wyjściowego testów.
/// </summary>
internal static class SceneSources
{
    /// <summary>Katalog wariantu sceny, np. <c>Variant("S13Sealed", "Step2")</c>.</summary>
    public static string Variant(string scene, string variant)
    {
        return Path.Combine(CSharpRoot(), "src", "Training.Workshop", "M5", scene, variant);
    }

    /// <summary>
    /// Pliki .cs wariantu z przestrzenią nazw wariantu zamienioną na <paramref name="newNamespace"/>
    /// (te same nazwy typów w kolejnych wersjach "biblioteki").
    /// </summary>
    public static IReadOnlyList<string> Rewritten(string scene, string variant, string newNamespace, params string[] skip)
    {
        var oldNamespace = "Training.Workshop.M5." + scene + "." + variant;
        return Directory.GetFiles(Variant(scene, variant), "*.cs")
            .Where(file => !skip.Contains(Path.GetFileName(file)))
            .Order(StringComparer.Ordinal)
            .Select(file => File.ReadAllText(file).Replace(oldNamespace, newNamespace))
            .ToList();
    }

    private static string CSharpRoot([CallerFilePath] string thisFile = "")
    {
        var dir = new DirectoryInfo(Path.GetDirectoryName(thisFile)!);
        while (dir != null && !File.Exists(Path.Combine(dir.FullName, "RefactoringLegacy.slnx")))
        {
            dir = dir.Parent;
        }
        return dir?.FullName ?? throw new DirectoryNotFoundException("nie znaleziono katalogu csharp/");
    }
}
