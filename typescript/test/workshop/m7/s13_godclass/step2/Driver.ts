import { CinemaManager } from '../../../../../src/workshop/m7/s13_godclass/step2/CinemaManager.js';
import { LegacyDb } from '../../../../../src/workshop/m7/s13_godclass/step2/LegacyDb.js';
import { LegacyMailer } from '../../../../../src/workshop/m7/s13_godclass/step2/LegacyMailer.js';
import { LegacyPaymentGateway } from '../../../../../src/workshop/m7/s13_godclass/step2/LegacyPaymentGateway.js';
import type { LocalDate, LocalDateTime } from '../../../../../src/workshop/shared/time.js';
import type { CinemaUnderTest } from '../CinemaUnderTest.js';

/** Adapter wariantu step2 dla S13Script (dostęp do haka CinemaManager.clock i globalnego stanu). */
export class Driver implements CinemaUnderTest {
  private cinema = new CinemaManager();

  reset(): void {
    LegacyDb.clear();
    this.cinema = new CinemaManager();
  }

  clock(clock: () => LocalDateTime): void {
    CinemaManager.clock = clock;
  }

  addScreening(id: string, title: string, format: number, start: LocalDateTime, rows: number,
    seatsPerRow: number, vipFromRow: number): void {
    this.cinema.addScreening(id, title, format, start, rows, seatsPerRow, vipFromRow);
  }

  book(screeningId: string, email: string, phone: string | null, seats: string[], types: string[],
    web: boolean, ownGlasses: boolean): string {
    return this.cinema.book(screeningId, email, phone, seats, types, web, ownGlasses);
  }

  pay(bookingId: string, card: string | null): string {
    return this.cinema.pay(bookingId, card);
  }

  cancel(bookingId: string): string {
    return this.cinema.cancel(bookingId);
  }

  expireOld(): void {
    this.cinema.expireOld();
  }

  use(bookingId: string): string {
    return this.cinema.use(bookingId);
  }

  loyaltyPoints(email: string): number {
    return this.cinema.loyaltyPoints(email);
  }

  freeSeats(screeningId: string): string[] {
    return this.cinema.freeSeats(screeningId);
  }

  dailyReport(day: LocalDate): string {
    return this.cinema.dailyReport(day);
  }

  settlement(title: string, week: number): string {
    return this.cinema.settlement(title, week);
  }

  sentMessages(): readonly string[] {
    return Object.freeze([...LegacyMailer.SENT]);
  }

  gatewayOperations(): readonly string[] {
    return Object.freeze([...LegacyPaymentGateway.CHARGES]);
  }
}
