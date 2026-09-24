using Training.Workshop.Shared;

namespace Training.Workshop.M6.S16TemplateMethod;

/// <summary>Stabilny kontrakt sceny: sprzedaż na jeden seans dnia.</summary>
public sealed record Sale(TimeOnly Time, string Title, int Tickets, Money Amount);
