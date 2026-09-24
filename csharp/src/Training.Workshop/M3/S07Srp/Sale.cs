namespace Training.Workshop.M3.S07Srp;

/// <summary>Stabilny kontrakt sceny - sprzedaż na jeden seans (kwoty brutto).</summary>
/// <param name="Title">tytuł filmu</param>
/// <param name="Tickets">liczba sprzedanych biletów</param>
/// <param name="TicketRevenue">przychód z biletów (VAT 8%)</param>
/// <param name="BarRevenue">przychód z baru (VAT 23%)</param>
public sealed record Sale(string Title, int Tickets, decimal TicketRevenue, decimal BarRevenue);
