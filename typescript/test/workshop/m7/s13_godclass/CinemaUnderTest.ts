import type { LocalDate, LocalDateTime } from '../../../../src/workshop/shared/time.js';

/**
 * Publiczne API CinemaManager plus dostęp do globalnego stanu i efektów ubocznych.
 * Każdy wariant sceny ma adapter Driver we własnym katalogu testowym (dostęp do haka clock).
 * Wszystkie warianty zachowują to samo API - to warunek kampanii Remove God Class.
 */
export interface CinemaUnderTest {
  reset(): void;

  clock(clock: () => LocalDateTime): void;

  addScreening(id: string, title: string, format: number, start: LocalDateTime, rows: number,
    seatsPerRow: number, vipFromRow: number): void;

  book(screeningId: string, email: string, phone: string | null, seats: string[], types: string[],
    web: boolean, ownGlasses: boolean): string;

  pay(bookingId: string, card: string | null): string;

  cancel(bookingId: string): string;

  expireOld(): void;

  use(bookingId: string): string;

  loyaltyPoints(email: string): number;

  freeSeats(screeningId: string): string[];

  dailyReport(day: LocalDate): string;

  settlement(title: string, week: number): string;

  sentMessages(): readonly string[];

  gatewayOperations(): readonly string[];
}
