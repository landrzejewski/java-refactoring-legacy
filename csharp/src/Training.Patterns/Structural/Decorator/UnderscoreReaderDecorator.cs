using System.Text.RegularExpressions;

namespace Training.Patterns.Structural.Decorator;

public partial class UnderscoreReaderDecorator : ReaderDecorator
{
    public UnderscoreReaderDecorator(IReader reader) : base(reader)
    {
    }

    public override string GetText() => Whitespace().Replace(base.GetText(), "_");

    [GeneratedRegex(@"\s")]
    private static partial Regex Whitespace();
}
