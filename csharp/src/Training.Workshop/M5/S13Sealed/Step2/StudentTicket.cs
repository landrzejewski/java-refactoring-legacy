using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step2;

/// <summary>Krok 2: bilet studencki (25%).</summary>
public sealed record StudentTicket(Money BasePrice) : Ticket(BasePrice);
