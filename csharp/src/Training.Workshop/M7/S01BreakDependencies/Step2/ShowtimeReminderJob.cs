using System.Globalization;

namespace Training.Workshop.M7.S01BreakDependencies.Step2;

/// <summary>
/// Krok 2: Parameterize Constructor z TimeProvider - czas przestaje być ukrytym wejściem.
/// Produkcja nadal używa zegara systemowego; zegar czytamy w tym samym miejscu co wcześniej.
/// </summary>
public sealed class ShowtimeReminderJob
{
    private readonly Func<IBookingStore> _stores;
    private readonly TimeProvider _clock;

    public ShowtimeReminderJob()
        : this(() => new LegacyDatabase(), TimeProvider.System)
    {
    }

    public ShowtimeReminderJob(Func<IBookingStore> stores, TimeProvider clock)
    {
        ArgumentNullException.ThrowIfNull(stores);
        ArgumentNullException.ThrowIfNull(clock);
        _stores = stores;
        _clock = clock;
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
                ReminderMailer.Send(booking.Email,
                    "Przypomnienie: " + booking.Title,
                    "Seans zaczyna sie o " + booking.Start.ToString("HH:mm", CultureInfo.InvariantCulture));
                database.MarkReminded(booking.Id);
                sent++;
            }
        }
        return sent;
    }
}
