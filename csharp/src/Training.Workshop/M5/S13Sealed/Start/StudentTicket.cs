using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Start;

/// <summary>Start: bilet studencki (25%).</summary>
public sealed record StudentTicket(Money BasePrice) : Ticket(BasePrice);
