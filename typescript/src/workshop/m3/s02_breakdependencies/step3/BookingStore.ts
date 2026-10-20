import type { PaidBooking } from '../PaidBooking.js';

/** Najwęższy kontrakt, którego potrzebuje ShowtimeReminderJob (wydzielony z LegacyDatabase). */
export interface BookingStore {
  paidBookings(): readonly PaidBooking[];

  markReminded(bookingId: string): void;
}
