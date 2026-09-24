using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step2;

/// <summary>Krok 2: bilet normalny (0%).</summary>
public sealed record StandardTicket(Money BasePrice) : Ticket(BasePrice);
