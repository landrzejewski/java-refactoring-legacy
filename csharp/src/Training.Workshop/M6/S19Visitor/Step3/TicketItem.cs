using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Step3;

/// <summary>Krok 3: bilet (VAT 8%) - czyste dane, bez Accept.</summary>
public sealed record TicketItem(string Title, string Format, Money Price) : IOrderItem;
