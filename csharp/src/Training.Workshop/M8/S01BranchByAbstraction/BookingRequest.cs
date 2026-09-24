namespace Training.Workshop.M8.S01BranchByAbstraction;

/// <summary>Stabilny kontrakt sceny: żądanie rezerwacji.</summary>
/// <param name="Seats">miejsca w formacie litera + rząd, np. "A10"</param>
/// <param name="Types">typy biletów legacy: N, S (student), E (senior), C (dziecko)</param>
public sealed record BookingRequest(
    Screening Screening, IReadOnlyList<string> Seats, IReadOnlyList<string> Types, bool Web, bool OwnGlasses);
