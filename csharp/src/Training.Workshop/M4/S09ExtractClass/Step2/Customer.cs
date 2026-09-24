using System.Text.RegularExpressions;

namespace Training.Workshop.M4.S09ExtractClass.Step2;

/// <summary>
/// Krok 2: Move Method <c>Contact</c> i <c>FormattedPhone</c> do Customer.
/// Klient sam wie, jak się go przedstawia - dane i zachowanie mają jednego właściciela.
/// </summary>
public sealed record Customer(string Name, string Email, string Phone)
{
    public string ContactLine()
    {
        return Name + " <" + Email.Trim().ToLowerInvariant() + ">, tel. " + FormattedPhone();
    }

    private string FormattedPhone()
    {
        string digits = Regex.Replace(Phone, @"\D", "");
        string local = digits[^9..];
        return local[..3] + "-" + local[3..6] + "-" + local[6..];
    }
}
