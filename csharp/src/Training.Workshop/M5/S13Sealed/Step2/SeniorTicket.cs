using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step2;

/// <summary>Krok 2: bilet seniora (30%).</summary>
public sealed record SeniorTicket(Money BasePrice) : Ticket(BasePrice);
