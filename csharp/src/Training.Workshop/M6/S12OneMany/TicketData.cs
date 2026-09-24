using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany;

/// <summary>Stabilny kontrakt sceny: zapłacona cena biletu i start seansu.</summary>
public sealed record TicketData(Money Price, DateTime ShowStart);
