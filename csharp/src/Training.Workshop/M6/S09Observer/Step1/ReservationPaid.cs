using Training.Workshop.Shared;

namespace Training.Workshop.M6.S09Observer.Step1;

/// <summary>Krok 1: zdarzenie - fakt "rezerwacja opłacona" z danymi potrzebnymi odbiorcom.</summary>
public sealed record ReservationPaid(string ReservationId, string Email, string Phone, Money Amount);
