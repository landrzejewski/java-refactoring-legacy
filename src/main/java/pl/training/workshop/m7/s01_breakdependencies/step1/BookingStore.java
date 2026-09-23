package pl.training.workshop.m7.s01_breakdependencies.step1;

import java.util.List;

import pl.training.workshop.m7.s01_breakdependencies.PaidBooking;

/** Najwęższy kontrakt, którego potrzebuje ShowtimeReminderJob (wydzielony z LegacyDatabase). */
public interface BookingStore {
    List<PaidBooking> paidBookings();

    void markReminded(String bookingId);
}
