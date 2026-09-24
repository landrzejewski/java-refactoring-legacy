namespace Training.Workshop.M3.S06Yagni;

/// <summary>Stabilny kontrakt sceny - dane do wyceny miejsca.</summary>
/// <param name="Format">2D, 3D albo IMAX</param>
/// <param name="Start">godzina seansu (przed 12:00 - seans poranny, -5.00)</param>
/// <param name="Row">rząd miejsca</param>
/// <param name="VipFromRow">od tego rzędu miejsce jest VIP (+10.00)</param>
public sealed record TicketQuote(string Format, TimeOnly Start, int Row, int VipFromRow);
