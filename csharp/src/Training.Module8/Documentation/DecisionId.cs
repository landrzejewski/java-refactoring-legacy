using System.Text.RegularExpressions;

namespace Training.Module8.Documentation;

public sealed partial record DecisionId
{
    public DecisionId(string value)
    {
        ArgumentNullException.ThrowIfNull(value);
        if (!Format().IsMatch(value))
        {
            throw new ArgumentException(
                "value must use the format ADR-NNNN");
        }
        Value = value;
    }

    public string Value { get; }

    public override string ToString() => Value;

    [GeneratedRegex(@"^ADR-[0-9]{4}\z")]
    private static partial Regex Format();
}
