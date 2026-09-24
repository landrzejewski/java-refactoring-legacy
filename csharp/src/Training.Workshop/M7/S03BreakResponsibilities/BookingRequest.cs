namespace Training.Workshop.M7.S03BreakResponsibilities;

/// <summary>Stabilny kontrakt sceny: prośba o rezerwację.</summary>
/// <param name="Email">adres klienta (może być null)</param>
/// <param name="Format">2D, 3D albo IMAX</param>
/// <param name="Seats">miejsca w postaci litera + rząd, np. C10 (rząd 10 i dalej to VIP)</param>
public sealed record BookingRequest(string? Email, string Format, IReadOnlyList<string> Seats);
