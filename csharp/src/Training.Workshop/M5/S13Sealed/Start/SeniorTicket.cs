using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Start;

/// <summary>Start: bilet seniora (30%).</summary>
public sealed record SeniorTicket(Money BasePrice) : Ticket(BasePrice);
