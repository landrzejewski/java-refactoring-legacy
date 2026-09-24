using Training.Workshop.Shared;

namespace Training.Workshop.M6.S09Observer.Step2;

/// <summary>Krok 2: bez zmian - zdarzenie - fakt "rezerwacja opłacona" z danymi potrzebnymi odbiorcom.</summary>
public sealed record ReservationPaid(string ReservationId, string Email, string Phone, Money Amount);
