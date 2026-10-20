using Training.Workshop.Shared;

namespace Training.Workshop.M5.S28Chain;

/// <summary>Stabilny kontrakt sceny: prośba o zwrot za bilet - kwota, początek seansu, moment prośby.</summary>
public sealed record RefundRequest
{
    public RefundRequest(Money amount, DateTime screeningStart, DateTime requestedAt)
    {
        ArgumentNullException.ThrowIfNull(amount);
        Amount = amount;
        ScreeningStart = screeningStart;
        RequestedAt = requestedAt;
    }

    public Money Amount { get; }

    public DateTime ScreeningStart { get; }

    public DateTime RequestedAt { get; }
}
