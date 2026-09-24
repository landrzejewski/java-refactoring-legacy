namespace Training.Workshop.M4.S01Rename;

/// <summary>Stabilny kontrakt sceny: jedna sprzedaż biletów na film.</summary>
public sealed record Sale(string Title, int Tickets, decimal Amount, bool Online);
