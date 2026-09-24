namespace Training.Workshop.M3.S11Dip;

/// <summary>Stabilny kontrakt sceny - rezerwacja do potwierdzenia.</summary>
public sealed record Reservation(string Email, string Title, DateTime Start, int Seats);
