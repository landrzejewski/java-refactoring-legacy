using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Start;

/// <summary>Start: produkt baru (VAT 23%).</summary>
public sealed record SnackItem(string Name, Money Price) : IOrderItem;
