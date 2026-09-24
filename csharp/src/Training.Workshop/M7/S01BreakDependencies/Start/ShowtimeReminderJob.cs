using System.Globalization;

namespace Training.Workshop.M7.S01BreakDependencies.Start;

/// <summary>
/// Start: zadanie wysyła przypomnienia na 2 godziny przed seansem.
/// Zapach: wszystkie zależności są "zaszyte" w środku - new LegacyDatabase(),
/// DateTime.Now i statyczny ReminderMailer. Tej klasy nie da się uruchomić w teście.
/// </summary>
public sealed class ShowtimeReminderJob
{
    public int Run()
    {
        var database = new LegacyDatabase();
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
