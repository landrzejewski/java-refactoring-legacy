using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace Training.Workshop.M8.S09Codemod.Step1;

/// <summary>
/// Krok 1: wyszukiwanie przez AST (drzewo składni Roslyn). Parser widzi wywołania metod,
/// a nie linie tekstu: komentarz przestaje być trafieniem, wywołanie na trzech liniach jest
/// znalezione. Przepisanie nadal tekstowe (bez zmian od startu).
/// </summary>
public sealed class BookCallCodemod
{
    /// <summary>Źródła projektu (API) - samo parsowanie składni ich nie potrzebuje.</summary>
    public BookCallCodemod(IReadOnlyList<string> projectSources)
    {
    }

    public IReadOnlyList<int> FindLines(string source)
    {
        CompilationUnitSyntax unit = CSharpSyntaxTree.ParseText(source).GetCompilationUnitRoot();
        return unit.DescendantNodes()
            .OfType<InvocationExpressionSyntax>()
            .Where(LooksLikeOldBook)
            .Select(call => call.GetLocation().GetLineSpan().StartLinePosition.Line + 1)
            .ToList();
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

    /// <summary>Składnia: metoda o nazwie Book z sześcioma argumentami. Typu odbiorcy nie znamy.</summary>
    private static bool LooksLikeOldBook(InvocationExpressionSyntax call)
    {
        return call.Expression is MemberAccessExpressionSyntax select
            && select.Name.Identifier.ValueText == "Book"
            && call.ArgumentList.Arguments.Count == 6;
    }
}
