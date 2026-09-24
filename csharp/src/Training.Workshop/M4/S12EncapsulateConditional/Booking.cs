using Training.Workshop.Shared;

namespace Training.Workshop.M4.S12EncapsulateConditional;

/// <summary>Stabilny kontrakt sceny.</summary>
/// <param name="Status">NEW, PAID, USED, EXPIRED, CANCELLED</param>
/// <param name="ScreeningStart">początek seansu</param>
/// <param name="Tickets">zapłacone za bilety (bez opłat rezerwacyjnych - te nie podlegają zwrotowi)</param>
/// <param name="Promo">kod promocji albo <c>null</c>; kody "FREE..." to bilety darmowe (bez zwrotu)</param>
public sealed record Booking(string Status, DateTime ScreeningStart, Money Tickets, string? Promo);
