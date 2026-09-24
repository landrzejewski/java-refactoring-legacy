namespace Training.Workshop.M4.S09ExtractClass.Step1;

/// <summary>
/// Krok 1 (ZŁY WYNIK pośredni): Extract Class przeniosło tylko DANE klienta.
/// Worek z właściwościami - cała wiedza o formatowaniu kontaktu nadal siedzi w Booking.
/// Gdyby tu się zatrzymać, mamy o jedną klasę więcej i żadnej nowej odpowiedzialności.
/// </summary>
public sealed record Customer(string Name, string Email, string Phone);
