using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S29BoundaryObject.Start;

/// <summary>
/// Start: surowa odpowiedź terminala "OK;AUTH-R1;4500" krąży po serwisie. Każda metoda sama ją
/// rozbiera (Split, StartsWith, LastIndexOf), sama zgaduje znaczenie pól i sama przelicza grosze.
/// Format obcego systemu przecieka do logiki, a błędna odpowiedź wybucha daleko od granicy.
/// </summary>
public sealed class PaymentService
{
    private readonly LegacyTerminalClient _terminal;
    private readonly Dictionary<string, string> _responses = [];

    public PaymentService(LegacyTerminalClient terminal)
    {
        ArgumentNullException.ThrowIfNull(terminal);
        _terminal = terminal;
    }

    public string Pay(string bookingId, Money amount)
    {
        var grosze = decimal.ToInt64(amount.Amount * 100);
        var raw = _terminal.Authorize(bookingId, grosze);
        _responses[bookingId] = raw;
        var parts = raw.Split(';');
        if (parts[0] != "OK")
        {
            return parts[0] + " " + parts[1];
        }
        var authorized = long.Parse(parts[2], CultureInfo.InvariantCulture);
        if (authorized < grosze)
        {
            return "PARTIAL " + parts[1] + " brakuje "
                + ((grosze - authorized) / 100m).ToString("0.00", CultureInfo.InvariantCulture);
        }
        return "PAID " + parts[1];
    }

    public string Receipt(string bookingId)
    {
        if (!_responses.TryGetValue(bookingId, out var raw))
        {
            return "Brak platnosci " + bookingId;
        }
        var parts = raw.Split(';');
        if (!raw.StartsWith("OK", StringComparison.Ordinal))
        {
            return "Platnosc " + bookingId + " nieudana: " + parts[1];
        }
        return "Platnosc " + bookingId + ": "
            + (decimal.Parse(parts[2], CultureInfo.InvariantCulture) / 100m).ToString("0.00", CultureInfo.InvariantCulture)
            + " (" + parts[1] + ")";
    }

    public Money TotalAuthorized()
    {
        var total = Money.Zero;
        foreach (var raw in _responses.Values)
        {
            if (raw.StartsWith("OK", StringComparison.Ordinal))
            {
                total = total.Plus(new Money(decimal.Parse(raw[(raw.LastIndexOf(';') + 1)..], CultureInfo.InvariantCulture) / 100m));
            }
        }
        return total;
    }
}
