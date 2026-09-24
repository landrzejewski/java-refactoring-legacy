namespace Training.Workshop.M4.S07MoveMethod.Start;

/// <summary>Rezerwacja jednego miejsca.</summary>
/// <param name="Id">identyfikator rezerwacji</param>
/// <param name="Screening">seans</param>
/// <param name="Seat">numer miejsca; <c>int?</c>, bo tak przychodzi ze starego API</param>
public sealed record Booking(string Id, Screening Screening, int? Seat);
