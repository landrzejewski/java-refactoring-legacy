namespace Training.Workshop.M3.S12CleanArchitecture.Step2;

/// <summary>Krok 2: dane przekraczające granicę do portu zapisu - rekord, nie object[].</summary>
public sealed record NewReservation(string Email, string Format, int Seats, decimal Total);
