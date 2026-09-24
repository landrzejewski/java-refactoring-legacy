namespace Training.Workshop.M4.S03MagicNumbers;

/// <summary>Stabilny kontrakt sceny: bilet z legacy kodem typu ("N", "S", "E", "C") i rzędem.</summary>
public sealed record Ticket(string Type, int Row);
