using System.Globalization;

namespace Training.Workshop.M3.S12CleanArchitecture.Step2;

/// <summary>
/// Adapter wejściowy: tłumaczy parametry na <see cref="BookSeatsCommand"/>, wynik i wyjątki
/// na kody odpowiedzi. Na razie składa też graf obiektów - przeniesiemy to w kroku 4.
/// </summary>
public sealed class ReservationController
{
    private readonly BookSeats _bookSeats;

    public ReservationController(RowStore db, Outbox outbox)
    {
        _bookSeats = new BookSeats(
            new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
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
