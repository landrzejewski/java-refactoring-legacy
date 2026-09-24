namespace Training.Workshop.M7.S01BreakDependencies.Step4;

/// <summary>Najwęższy kontrakt, którego potrzebuje ShowtimeReminderJob (wydzielony z LegacyDatabase).</summary>
public interface IBookingStore
{
    IReadOnlyList<PaidBooking> PaidBookings();

    void MarkReminded(string bookingId);
}
