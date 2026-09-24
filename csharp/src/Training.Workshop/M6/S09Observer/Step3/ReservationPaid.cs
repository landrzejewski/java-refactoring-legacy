using Training.Workshop.Shared;

namespace Training.Workshop.M6.S09Observer.Step3;

/// <summary>Krok 3: bez zmian - zdarzenie - fakt "rezerwacja opłacona" z danymi potrzebnymi odbiorcom.</summary>
public sealed record ReservationPaid(string ReservationId, string Email, string Phone, Money Amount);
