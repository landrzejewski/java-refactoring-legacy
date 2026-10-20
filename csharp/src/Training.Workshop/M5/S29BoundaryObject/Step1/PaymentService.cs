using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S29BoundaryObject.Step1;

/// <summary>
/// Krok 1: odpowiedź jest rozbierana RAZ, zaraz po wywołaniu terminala, do PaymentConfirmation.
/// Serwis przechowuje i czyta już tylko obiekt graniczny; parsowanie jest jeszcze prywatną
/// metodą serwisu - do przeniesienia w kroku 2.
/// </summary>
public sealed class PaymentService
{
    private readonly LegacyTerminalClient _terminal;
    private readonly Dictionary<string, PaymentConfirmation> _confirmations = [];

    public PaymentService(LegacyTerminalClient terminal)
    {
        ArgumentNullException.ThrowIfNull(terminal);
        _terminal = terminal;
    }

    public string Pay(string bookingId, Money amount)
    {
        var grosze = decimal.ToInt64(amount.Amount * 100);
        var confirmation = Parse(_terminal.Authorize(bookingId, grosze));
        _confirmations[bookingId] = confirmation;
        if (!confirmation.Approved)
        {
            return confirmation.Outcome.ToString().ToUpperInvariant() + " " + confirmation.Code;
        }
        if (confirmation.IsPartialFor(amount))
        {
            return "PARTIAL " + confirmation.Code + " brakuje " + amount.Minus(confirmation.Authorized);
        }
        return "PAID " + confirmation.Code;
    }

    public string Receipt(string bookingId)
    {
        if (!_confirmations.TryGetValue(bookingId, out var confirmation))
        {
            return "Brak platnosci " + bookingId;
        }
        if (!confirmation.Approved)
        {
            return "Platnosc " + bookingId + " nieudana: " + confirmation.Code;
        }
        return "Platnosc " + bookingId + ": " + confirmation.Authorized + " (" + confirmation.Code + ")";
    }

    public Money TotalAuthorized()
    {
        var total = Money.Zero;
        foreach (var confirmation in _confirmations.Values)
        {
            total = total.Plus(confirmation.Authorized);
        }
        return total;
    }

    private static PaymentConfirmation Parse(string raw)
    {
        var parts = raw.Split(';');
        if (parts.Length != 3)
        {
            throw new ArgumentException("malformed terminal response: " + raw);
        }
        var outcome = parts[0] switch
        {
            "OK" => Outcome.Approved,
            "DECLINED" => Outcome.Declined,
            "ERROR" => Outcome.Error,
            _ => throw new ArgumentException("malformed terminal response: " + raw),
        };
        if (!decimal.TryParse(parts[2], NumberStyles.AllowLeadingSign | NumberStyles.AllowDecimalPoint | NumberStyles.AllowExponent, CultureInfo.InvariantCulture, out var grosze))
        {
            throw new ArgumentException("malformed terminal response: " + raw);
        }
        return new PaymentConfirmation(outcome, parts[1], new Money(grosze / 100m));
    }
}
