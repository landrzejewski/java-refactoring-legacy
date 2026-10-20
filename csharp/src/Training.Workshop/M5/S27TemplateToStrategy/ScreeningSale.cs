using Training.Workshop.Shared;

namespace Training.Workshop.M5.S27TemplateToStrategy;

/// <summary>Stabilny kontrakt sceny: sprzedaż biletów na jeden seans danego dnia.</summary>
public sealed record ScreeningSale(DateOnly Day, string Title, int Tickets, Money Revenue);
