import type { Clock } from './Clock.js';
import { systemClock } from './Clock.js';

const MILLIS_PER_DAY = 86_400_000;

// Minimalny odpowiednik java.time.LocalDate (data bez czasu, strefa UTC).
export class LocalDate {
  private constructor(private readonly epochDay: number) {}

  static of(year: number, month: number, day: number): LocalDate {
    return new LocalDate(Date.UTC(year, month - 1, day) / MILLIS_PER_DAY);
  }

  // Odpowiednik LocalDate.now(clock) / LocalDate.now(ZoneOffset.UTC).
  static now(clock: Clock = systemClock): LocalDate {
    return new LocalDate(Math.floor(clock.now().getTime() / MILLIS_PER_DAY));
  }

  plusDays(days: number): LocalDate {
    return new LocalDate(this.epochDay + days);
  }

  isAfter(other: LocalDate): boolean {
    return this.epochDay > other.epochDay;
  }

  equals(other: LocalDate): boolean {
    return this.epochDay === other.epochDay;
  }

  toString(): string {
    return new Date(this.epochDay * MILLIS_PER_DAY).toISOString().slice(0, 10);
  }
}
