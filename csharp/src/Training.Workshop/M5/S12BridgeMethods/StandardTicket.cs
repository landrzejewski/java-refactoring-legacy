using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods;

/// <summary>Stabilny kontrakt sceny: bilet normalny.</summary>
public sealed record StandardTicket(Money BasePrice) : ITicket;
