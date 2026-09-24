using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step3;

/// <summary>Krok 3: bilet studencki (25%).</summary>
public sealed record StudentTicket(Money BasePrice) : Ticket(BasePrice);
