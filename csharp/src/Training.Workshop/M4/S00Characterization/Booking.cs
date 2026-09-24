namespace Training.Workshop.M4.S00Characterization;

/// <summary>
/// Stabilny kontrakt sceny - wspólny dla Start i wszystkich kroków.
/// </summary>
/// <param name="Customer">adres klienta</param>
/// <param name="Title">tytuł filmu</param>
/// <param name="Format">legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX</param>
/// <param name="Start">godzina seansu</param>
/// <param name="TicketTypes">legacy kody biletów: "N" normalny, "S" student, "E" senior, "C" dziecko</param>
/// <param name="Online">rezerwacja przez internet (opłata rezerwacyjna) albo w kasie</param>
public sealed record Booking(string Customer, string Title, int Format, TimeOnly Start,
    IReadOnlyList<string> TicketTypes, bool Online);
