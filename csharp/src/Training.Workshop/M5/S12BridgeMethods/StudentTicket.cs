using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods;

/// <summary>Stabilny kontrakt sceny: bilet studencki.</summary>
public sealed record StudentTicket(Money BasePrice, string StudentId) : ITicket;
