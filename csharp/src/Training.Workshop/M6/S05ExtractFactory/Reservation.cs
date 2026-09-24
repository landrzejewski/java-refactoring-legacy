using Training.Workshop.Shared;

namespace Training.Workshop.M6.S05ExtractFactory;

/// <summary>
/// Stabilny kontrakt sceny: utworzona rezerwacja. Kasa płaci od razu, więc ExpiresAt == null.
/// </summary>
public sealed record Reservation(
    string Id,
    string Channel,
    string Email,
    IReadOnlyList<string> Seats,
    Money Fee,
    DateTime? ExpiresAt);
