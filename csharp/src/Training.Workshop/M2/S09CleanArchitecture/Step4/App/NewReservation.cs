namespace Training.Workshop.M2.S09CleanArchitecture.Step4.App;

/// <summary>Krok 2: dane przekraczające granicę do portu zapisu - rekord, nie object[].</summary>
public sealed record NewReservation(string Email, string Format, int Seats, decimal Total);
