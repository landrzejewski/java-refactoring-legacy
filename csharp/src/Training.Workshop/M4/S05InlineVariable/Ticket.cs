namespace Training.Workshop.M4.S05InlineVariable;

/// <summary>Stabilny kontrakt sceny: wystawiony bilet.</summary>
/// <param name="Code">kod biletu</param>
/// <param name="Label">etykieta drukowana na bilecie</param>
/// <param name="IssuedAt">chwila wystawienia</param>
/// <param name="HoldUntil">do kiedy rezerwacja czeka na płatność (15 minut od wystawienia)</param>
public sealed record Ticket(string Code, string Label, DateTimeOffset IssuedAt, DateTimeOffset HoldUntil);
