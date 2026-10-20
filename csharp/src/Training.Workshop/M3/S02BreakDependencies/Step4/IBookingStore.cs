namespace Training.Workshop.M3.S02BreakDependencies.Step4;

/// <summary>Najwęższy kontrakt, którego potrzebuje ShowtimeReminderJob (wydzielony z LegacyDatabase).</summary>
public interface IBookingStore
{
    IReadOnlyList<PaidBooking> PaidBookings();

    void MarkReminded(string bookingId);
}
