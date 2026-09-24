namespace Training.Workshop.M8.S05BoyScout;

/// <summary>Stabilny kontrakt sceny: dane biletu do wydruku (kwota jak w legacy - double).</summary>
/// <param name="Phone">może być null - klient nie podał telefonu</param>
public sealed record Ticket(string Title, DateTime Start, IReadOnlyList<string> Seats, string Email, string? Phone,
    double Total);
