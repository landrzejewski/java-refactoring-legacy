namespace Training.Workshop.M7.S01BreakDependencies;

/// <summary>Stabilny kontrakt sceny: opłacona rezerwacja, której może dotyczyć przypomnienie.</summary>
public sealed record PaidBooking(string Id, string Email, string Title, DateTime Start, bool Reminded);
