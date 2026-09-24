namespace Training.Workshop.M4.S03MagicNumbers;

/// <summary>Stabilny kontrakt sceny.</summary>
/// <param name="Format">legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX</param>
/// <param name="Start">godzina seansu</param>
/// <param name="Online">zamówienie przez internet (opłata rezerwacyjna za bilet)</param>
/// <param name="Tickets">bilety zamówienia</param>
public sealed record Order(int Format, TimeOnly Start, bool Online, IReadOnlyList<Ticket> Tickets);
