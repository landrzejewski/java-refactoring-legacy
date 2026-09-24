namespace Training.Workshop.M7.S05BreakMethod;

/// <summary>Stabilny kontrakt sceny: seans w repertuarze dnia.</summary>
public sealed record Screening(string Title, string Format, TimeOnly Start, int Hall, bool Cancelled);
