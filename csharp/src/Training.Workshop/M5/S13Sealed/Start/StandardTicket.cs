using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Start;

/// <summary>Start: bilet normalny (0%).</summary>
public sealed record StandardTicket(Money BasePrice) : Ticket(BasePrice);
