namespace Training.Workshop.Tests.Support;

/// <summary>
/// Ścieżki do plików źródłowych warsztatu - dla testów, które czytają kod sceny
/// (odpowiednik Path.of("src/main/java/pl/training/workshop/...") w Javie).
/// </summary>
public static class SourceFiles
{
    /// <summary>Katalog csharp/ repozytorium (szukany w górę od katalogu wyjściowego testów).</summary>
    public static string CSharpRoot()
    {
        var dir = new DirectoryInfo(AppContext.BaseDirectory);
        while (dir != null && !File.Exists(Path.Combine(dir.FullName, "RefactoringLegacy.slnx")))
        {
            dir = dir.Parent;
        }
        return dir?.FullName ?? throw new DirectoryNotFoundException("nie znaleziono katalogu csharp/");
    }

    /// <summary>Ścieżka w projekcie Training.Workshop, np. <c>Workshop("M8", "S07Adr")</c>.</summary>
    public static string Workshop(params string[] parts)
    {
        return Path.Combine([CSharpRoot(), "src", "Training.Workshop", .. parts]);
    }

    /// <summary>Ścieżka w projekcie testów, np. <c>Tests("M8", "S10QualityGate")</c>.</summary>
    public static string Tests(params string[] parts)
    {
        return Path.Combine([CSharpRoot(), "tests", "Training.Workshop.Tests", .. parts]);
    }
}
