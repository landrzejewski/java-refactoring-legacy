using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step3;

/// <summary>Krok 3: bilet seniora (30%).</summary>
public sealed record SeniorTicket(Money BasePrice) : Ticket(BasePrice);
