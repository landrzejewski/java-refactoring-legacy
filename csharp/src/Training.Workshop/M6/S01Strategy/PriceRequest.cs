using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy;

/// <summary>
/// Stabilny kontrakt sceny: cena bazowa formatu, typ biletu (legacy: N, S, E, C)
/// i nazwa programu zniżek skonfigurowanego w kinie (STANDARD, STUDENT_WEEK, PREMIERE).
/// </summary>
public sealed record PriceRequest(Money Base, string TicketType, string? Program);
