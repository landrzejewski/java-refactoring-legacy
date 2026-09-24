using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step3;

/// <summary>Krok 3: NOWY typ biletu dziecięcego (40%) - kontrola wyczerpania wymusiła jego obsługę w PriceCalculator.</summary>
public sealed record ChildTicket(Money BasePrice) : Ticket(BasePrice);
