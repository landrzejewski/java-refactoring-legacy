namespace Training.Workshop.M3.S15Invariants.Step1;

/// <summary>
/// Krok 1: Remove Setting Method + konwersja na record (w IDE: "Convert to positional record").
/// Obiekt jest niezmienny i powstaje w całości w jednym wywołaniu - ale konstruktor
/// wciąż przyjmie wszystko.
/// </summary>
public sealed record Reservation(string Email, int Seats, decimal Total);
