using System.Globalization;
using Training.Workshop.M3.S12CleanArchitecture.Step4.App;

namespace Training.Workshop.M3.S12CleanArchitecture.Step4.Adapter;

/// <summary>
/// Krok 4: adapter wejściowy dostaje gotowy przypadek użycia - nie wie, jakie
/// adaptery wyjściowe stoją za portami. Tylko tłumaczy żądanie i odpowiedź.
/// </summary>
public sealed class ReservationController
{
    private readonly BookSeats _bookSeats;

    public ReservationController(BookSeats bookSeats)
    {
        _bookSeats = bookSeats;
    }

    public string Handle(IReadOnlyDictionary<string, string> parameters)
    {
        var email = parameters.GetValueOrDefault("email");
        if (string.IsNullOrWhiteSpace(email))
        {
            return "400 brak email";
        }
        var rows = parameters.GetValueOrDefault("rows", "").Split(',')
            .Where(s => !string.IsNullOrWhiteSpace(s)).Select(s => int.Parse(s, CultureInfo.InvariantCulture)).ToList();
        try
        {
            var booking = _bookSeats.Execute(
                new BookSeatsCommand(email, parameters.GetValueOrDefault("format", "2D"), rows));
            return "201 " + booking.Id + " " + booking.Total.ToString(CultureInfo.InvariantCulture);
        }
        catch (ArgumentException e)
        {
            return "400 " + e.Message;
        }
        catch (InvalidOperationException e)
        {
            return "503 " + e.Message;
        }
    }
}
