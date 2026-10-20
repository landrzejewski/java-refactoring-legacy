package pl.training.workshop.m3.s02_breakdependencies.step1;

import java.util.List;

import pl.training.workshop.m3.s02_breakdependencies.PaidBooking;

/** Najwęższy kontrakt, którego potrzebuje ShowtimeReminderJob (wydzielony z LegacyDatabase). */
public interface BookingStore {
    List<PaidBooking> paidBookings();

    void markReminded(String bookingId);
}
