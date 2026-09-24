using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Step3;

/// <summary>Krok 3: produkt baru (VAT 23%) - czyste dane, bez Accept.</summary>
public sealed record SnackItem(string Name, Money Price) : IOrderItem;
