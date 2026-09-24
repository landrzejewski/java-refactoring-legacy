using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Start;

/// <summary>Start: bilet (VAT 8%).</summary>
public sealed record TicketItem(string Title, string Format, Money Price) : IOrderItem;
