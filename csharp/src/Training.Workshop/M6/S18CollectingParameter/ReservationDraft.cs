namespace Training.Workshop.M6.S18CollectingParameter;

/// <summary>Stabilny kontrakt sceny: szkic rezerwacji do walidacji (Now podawane jawnie - testowalność).</summary>
public sealed record ReservationDraft(string? Email, IReadOnlyList<string> Seats, DateTime ShowStart, DateTime Now);
