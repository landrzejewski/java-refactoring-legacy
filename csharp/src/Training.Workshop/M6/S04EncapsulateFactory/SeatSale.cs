using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory;

/// <summary>Stabilny kontrakt sceny: sprzedaż miejsc na jeden seans (rząd 10+ to VIP).</summary>
public sealed record SeatSale(string Title, Money Base, IReadOnlyList<int> Rows);
