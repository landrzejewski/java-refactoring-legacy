namespace Training.Workshop.M3.S02BreakDependencies;

/// <summary>Stabilny kontrakt sceny: opłacona rezerwacja, której może dotyczyć przypomnienie.</summary>
public sealed record PaidBooking(string Id, string Email, string Title, DateTime Start, bool Reminded);
