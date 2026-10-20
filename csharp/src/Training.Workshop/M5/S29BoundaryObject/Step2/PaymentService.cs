using Training.Workshop.Shared;

namespace Training.Workshop.M5.S29BoundaryObject.Step2;

/// <summary>
/// Krok 2: serwis nie zna już formatu terminala - grosze i tekst odpowiedzi tłumaczy mapper.
/// Nadal jednak sam woła LegacyTerminalClient i składa wywołanie z mapperem; to zostanie
/// schowane za portem w kroku 3.
/// </summary>
public sealed class PaymentService
{
    private readonly LegacyTerminalClient _terminal;
    private readonly TerminalResponseMapper _mapper = new();
    private readonly Dictionary<string, PaymentConfirmation> _confirmations = [];

    public PaymentService(LegacyTerminalClient terminal)
    {
        ArgumentNullException.ThrowIfNull(terminal);
        _terminal = terminal;
    }

    public string Pay(string bookingId, Money amount)
    {
        var raw = _terminal.Authorize(bookingId, _mapper.ToGrosze(amount));
        var confirmation = _mapper.ToConfirmation(raw);
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
}
