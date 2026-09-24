using System.Globalization;

namespace Training.Workshop.M7.S01BreakDependencies.Step1;

/// <summary>
/// Krok 1: Extract Interface + Parameterize Constructor - baza schowana za IBookingStore
/// i przekazywana z zewnątrz. Fabryka (Func) zachowuje czas życia: połączenie nadal
/// powstaje przy każdym Run(), a nie przy tworzeniu zadania.
/// </summary>
public sealed class ShowtimeReminderJob
{
    private readonly Func<IBookingStore> _stores;

    public ShowtimeReminderJob()
        : this(() => new LegacyDatabase())
    {
    }

    public ShowtimeReminderJob(Func<IBookingStore> stores)
    {
        ArgumentNullException.ThrowIfNull(stores);
        _stores = stores;
    }

    public int Run()
    {
        var database = _stores();
        var now = DateTime.Now;
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
