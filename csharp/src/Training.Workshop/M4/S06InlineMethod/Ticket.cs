namespace Training.Workshop.M4.S06InlineMethod;

/// <summary>Stabilny kontrakt sceny.</summary>
/// <param name="Format">legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX</param>
/// <param name="Start">godzina seansu</param>
public sealed record Ticket(int Format, TimeOnly Start);
