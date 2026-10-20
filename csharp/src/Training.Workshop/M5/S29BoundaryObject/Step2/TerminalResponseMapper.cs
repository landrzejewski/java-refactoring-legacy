using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S29BoundaryObject.Step2;

/// <summary>
/// Krok 2: mapper na granicy (Extract Class z prywatnej metody serwisu). Jedyne miejsce,
/// które zna format terminala: grosze w obie strony i tekst "STATUS;KOD;KWOTA".
/// Niepoprawna odpowiedź jest odrzucana tu, a nie trzy metody dalej.
/// </summary>
public sealed class TerminalResponseMapper
{
    public long ToGrosze(Money amount)
    {
        return decimal.ToInt64(amount.Amount * 100);
    }

    public PaymentConfirmation ToConfirmation(string raw)
    {
        var parts = raw.Split(';');
        if (parts.Length != 3)
        {
            throw Malformed(raw);
        }
        var outcome = parts[0] switch
        {
            "OK" => Outcome.Approved,
            "DECLINED" => Outcome.Declined,
            "ERROR" => Outcome.Error,
            _ => throw Malformed(raw),
        };
        if (!decimal.TryParse(parts[2], NumberStyles.AllowLeadingSign | NumberStyles.AllowDecimalPoint | NumberStyles.AllowExponent, CultureInfo.InvariantCulture, out var grosze))
        {
            throw Malformed(raw);
        }
        return new PaymentConfirmation(outcome, parts[1], new Money(grosze / 100m));
    }

    private static ArgumentException Malformed(string raw) => new("malformed terminal response: " + raw);
}
