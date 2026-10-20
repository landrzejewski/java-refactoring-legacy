namespace Training.Workshop.M5.S30Facade;

/// <summary>Stabilny kontrakt sceny: zamówienie biletów jednego typu na kilka miejsc jednego seansu.</summary>
public sealed record TicketOrder(string Screening, IReadOnlyList<string> Seats, string TicketType, string Card, string Email)
{
    public string Screening { get; } = Screening ?? throw new ArgumentNullException(nameof(Screening));

    public IReadOnlyList<string> Seats { get; } = [.. Seats];

    public string TicketType { get; } = TicketType ?? throw new ArgumentNullException(nameof(TicketType));
}
