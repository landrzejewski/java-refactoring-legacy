using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace Training.Workshop.M8.S09Codemod.Step2;

/// <summary>
/// Krok 2: przepisanie na drzewie składni (CSharpSyntaxRewriter) - podmieniamy dokładnie dwa ostatnie
/// argumenty (literał -&gt; stała enum, wyrażenie -&gt; operator warunkowy) i dopisujemy using. Formatowanie
/// reszty pliku zostaje. Nadal tylko składnia: HotelService.Book też zostanie "zmigrowany".
/// </summary>
public sealed class BookCallCodemod
{
    private const string NewTypesNamespace = "Cinema.Options";

    private sealed record Parsed(CompilationUnitSyntax Unit, IReadOnlyList<InvocationExpressionSyntax> Calls);

    /// <summary>Źródła projektu (API) - samo parsowanie składni ich nie potrzebuje.</summary>
    public BookCallCodemod(IReadOnlyList<string> projectSources)
    {
    }

    public IReadOnlyList<int> FindLines(string source)
    {
        return Parse(source).Calls
            .Select(call => call.GetLocation().GetLineSpan().StartLinePosition.Line + 1)
            .ToList();
    }

    public string Rewrite(string source)
    {
        Parsed parsed = Parse(source);
        if (parsed.Calls.Count == 0)
        {
            return source;
        }
        var rewriter = new BookCallRewriter(parsed.Calls);
        var unit = (CompilationUnitSyntax)rewriter.Visit(parsed.Unit);
        return WithUsing(unit).ToFullString();
    }

    /// <summary>Dopisuje using dla nowych typów (Channel, Glasses) za ostatnią dyrektywą using, jeśli go brakuje.</summary>
    private static CompilationUnitSyntax WithUsing(CompilationUnitSyntax unit)
    {
        if (unit.Usings.Any(directive => directive.Name?.ToString() == NewTypesNamespace))
        {
            return unit;
        }
        UsingDirectiveSyntax directive = SyntaxFactory.ParseCompilationUnit("using " + NewTypesNamespace + ";\n").Usings[0];
        return unit.AddUsings(directive);
    }

    /// <summary>
    /// Podmienia dokładnie dwa ostatnie argumenty wskazanych wywołań (literał -&gt; stała enum,
    /// wyrażenie -&gt; operator warunkowy), zachowując formatowanie (trivia) reszty pliku.
    /// </summary>
    private sealed class BookCallRewriter(IReadOnlyList<InvocationExpressionSyntax> calls) : CSharpSyntaxRewriter
    {
        public override SyntaxNode? VisitInvocationExpression(InvocationExpressionSyntax node)
        {
            var visited = (InvocationExpressionSyntax)base.VisitInvocationExpression(node)!;
            if (!calls.Contains(node))
            {
                return visited;
            }
            SeparatedSyntaxList<ArgumentSyntax> args = visited.ArgumentList.Arguments;
            args = args.Replace(args[4], Migrate(args[4], "Channel.Web", "Channel.BoxOffice"));
            args = args.Replace(args[5], Migrate(args[5], "Glasses.Own", "Glasses.Rented"));
            return visited.WithArgumentList(visited.ArgumentList.WithArguments(args));
        }

        private static ArgumentSyntax Migrate(ArgumentSyntax arg, string ifTrue, string ifFalse)
        {
            ExpressionSyntax expression = arg.Expression;
            string text;
            if (expression.IsKind(SyntaxKind.TrueLiteralExpression) || expression.IsKind(SyntaxKind.FalseLiteralExpression))
            {
                text = expression.IsKind(SyntaxKind.TrueLiteralExpression) ? ifTrue : ifFalse;
            }
            else
            {
                string original = expression.WithoutTrivia().ToString();
                bool simple = expression is IdentifierNameSyntax or MemberAccessExpressionSyntax
                    or InvocationExpressionSyntax;
                text = (simple ? original : "(" + original + ")") + " ? " + ifTrue + " : " + ifFalse;
            }
            return arg.WithExpression(SyntaxFactory.ParseExpression(text).WithTriviaFrom(expression));
        }
    }

    private static Parsed Parse(string source)
    {
        CompilationUnitSyntax unit = CSharpSyntaxTree.ParseText(source).GetCompilationUnitRoot();
        List<InvocationExpressionSyntax> calls = unit.DescendantNodes()
            .OfType<InvocationExpressionSyntax>()
            .Where(LooksLikeOldBook)
            .ToList();
        return new Parsed(unit, calls);
    }

    /// <summary>Składnia: metoda o nazwie Book z sześcioma argumentami. Typu odbiorcy nie znamy.</summary>
    private static bool LooksLikeOldBook(InvocationExpressionSyntax call)
    {
        return call.Expression is MemberAccessExpressionSyntax select
            && select.Name.Identifier.ValueText == "Book"
            && call.ArgumentList.Arguments.Count == 6;
    }
}
