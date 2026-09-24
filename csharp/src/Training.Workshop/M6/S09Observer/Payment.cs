using Training.Workshop.Shared;

namespace Training.Workshop.M6.S09Observer;

/// <summary>Stabilny kontrakt sceny: opłata za rezerwację (kwota biletów, bez opłat).</summary>
public sealed record Payment(string ReservationId, string Email, string Phone, Money Amount);
