using System.Globalization;

namespace Training.Workshop.M7.S01BreakDependencies.Step4;

/// <summary>
/// Krok 4 (rozwiązanie): seam z dziedziczenia zamieniony na zależność konstruktora
/// (ReminderSender). Pod ochroną testu z kroku 3 klasa znów jest sealed, a produkcja
/// w domyślnym konstruktorze nadal składa LegacyDatabase, zegar systemowy i ReminderMailer.
/// </summary>
public sealed class ShowtimeReminderJob
{
    private readonly Func<IBookingStore> _stores;
    private readonly TimeProvider _clock;
    private readonly ReminderSender _sender;

    public ShowtimeReminderJob()
        : this(() => new LegacyDatabase(), TimeProvider.System, ReminderMailer.Send)
    {
    }

    public ShowtimeReminderJob(Func<IBookingStore> stores, TimeProvider clock, ReminderSender sender)
    {
        ArgumentNullException.ThrowIfNull(stores);
        ArgumentNullException.ThrowIfNull(clock);
        ArgumentNullException.ThrowIfNull(sender);
        _stores = stores;
        _clock = clock;
        _sender = sender;
    }

    public int Run()
    {
        var database = _stores();
        var now = _clock.GetLocalNow().DateTime;
        var sent = 0;
        foreach (var booking in database.PaidBookings())
        {
            var minutes = (long)(booking.Start - now).TotalMinutes;
            if (!booking.Reminded && minutes > 0 && minutes <= 120)
            {
                _sender(booking.Email,
                    "Przypomnienie: " + booking.Title,
                    "Seans zaczyna sie o " + booking.Start.ToString("HH:mm", CultureInfo.InvariantCulture));
                database.MarkReminded(booking.Id);
                sent++;
            }
        }
        return sent;
    }
}
