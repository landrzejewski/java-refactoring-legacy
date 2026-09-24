using System.Globalization;

namespace Training.Workshop.M7.S01BreakDependencies.Step3;

/// <summary>
/// Krok 3: Subclass and Override Method - wywołanie statycznego mailera przeniesione
/// do chronionej metody wirtualnej SendReminder(). Test nadpisuje ją w podklasie (seam) i po raz
/// pierwszy widzi wysłane przypomnienia. Klasa przestała być sealed - to cena tego seamu.
/// </summary>
public class ShowtimeReminderJob
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
                SendReminder(booking.Email,
                    "Przypomnienie: " + booking.Title,
                    "Seans zaczyna sie o " + booking.Start.ToString("HH:mm", CultureInfo.InvariantCulture));
                database.MarkReminded(booking.Id);
                sent++;
            }
        }
        return sent;
    }

    protected virtual void SendReminder(string to, string subject, string body)
    {
        ReminderMailer.Send(to, subject, body);
    }
}
