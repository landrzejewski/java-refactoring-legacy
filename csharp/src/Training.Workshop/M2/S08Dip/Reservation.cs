namespace Training.Workshop.M2.S08Dip;

/// <summary>Stabilny kontrakt sceny - rezerwacja do potwierdzenia.</summary>
public sealed record Reservation(string Email, string Title, DateTime Start, int Seats);
