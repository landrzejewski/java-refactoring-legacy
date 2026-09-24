namespace Training.Workshop.M8.S04ShadowLimits;

/// <summary>Stabilny kontrakt sceny: rezerwacja online biletów 2D (25.00 + 2.00 opłaty za bilet).</summary>
public sealed record BookingRequest(string Email, string Card, string Title, int Tickets);
