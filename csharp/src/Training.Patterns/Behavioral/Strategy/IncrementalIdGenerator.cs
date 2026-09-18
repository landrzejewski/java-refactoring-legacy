using System.Globalization;

namespace Training.Patterns.Behavioral.Strategy;

public class IncrementalIdGenerator : IIdGenerator
{
    private const string PATTERN = "D20"; // Java: "%020d"

    private int counter;

    public string GetNext() => (++counter).ToString(PATTERN, CultureInfo.InvariantCulture);
}
