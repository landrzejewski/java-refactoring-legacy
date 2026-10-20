using Training.Workshop.Shared;

namespace Training.Workshop.M5.S29BoundaryObject.Step3;

/// <summary>
/// Krok 3: serwis zależy tylko od portu IPaymentTerminal i widzi wyłącznie PaymentConfirmation.
/// Granica jest kompletna: Adapter (kształt wywołania) + mapper (format danych)
/// + Boundary Object (poprawna wartość w języku domeny).
/// </summary>
public sealed class PaymentService
{
    private readonly IPaymentTerminal _terminal;
    private readonly Dictionary<string, PaymentConfirmation> _confirmations = [];

    public PaymentService(IPaymentTerminal terminal)
    {
        ArgumentNullException.ThrowIfNull(terminal);
        _terminal = terminal;
    }

    public string Pay(string bookingId, Money amount)
    {
        var confirmation = _terminal.Authorize(bookingId, amount);
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
