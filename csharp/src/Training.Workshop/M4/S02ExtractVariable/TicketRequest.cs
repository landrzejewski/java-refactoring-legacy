namespace Training.Workshop.M4.S02ExtractVariable;

/// <summary>Stabilny kontrakt sceny.</summary>
/// <param name="Format">legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX</param>
/// <param name="Type">legacy kod biletu: "N", "S" (student), "E" (senior), "C" (dziecko)</param>
/// <param name="Start">godzina seansu</param>
/// <param name="Row">rząd miejsca albo <c>null</c> dla wolnej widowni (bez numerowanych miejsc)</param>
/// <param name="OwnGlasses">klient ma własne okulary 3D</param>
public sealed record TicketRequest(int Format, string Type, TimeOnly Start, int? Row, bool OwnGlasses);
