namespace Training.Workshop.M6.S02Polymorphism;

/// <summary>
/// Stabilny kontrakt sceny: wiersz z bazy repertuaru. Znaczenie <c>Value</c> zależy od rodzaju:
/// REGULAR i PREMIERE - długość filmu w minutach, MARATHON - liczba filmów.
/// </summary>
public sealed record ScreeningRow(string Kind, string Title, int Value);
