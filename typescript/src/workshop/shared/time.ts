// Minimalne odpowiedniki java.time używane w warsztacie CineLegacy.
// Wszystko liczone w UTC, więc wynik nie zależy od strefy czasowej maszyny.

const MILLIS_PER_MINUTE = 60_000;
const MILLIS_PER_HOUR = 3_600_000;
const MILLIS_PER_DAY = 86_400_000;

export const DAYS_OF_WEEK = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'] as const;
export type DayOfWeek = (typeof DAYS_OF_WEEK)[number];

const pad = (value: number, width = 2): string => String(value).padStart(width, '0');

function dayOfWeekOf(epochMillis: number): DayOfWeek {
  // 1970-01-01 był czwartkiem (indeks 3).
  const index = (Math.floor(epochMillis / MILLIS_PER_DAY) + 3) % 7;
  return DAYS_OF_WEEK[(index + 7) % 7]!;
}

// Odpowiednik java.time.LocalTime (godzina i minuta).
export class LocalTime {
  static readonly MIDNIGHT = new LocalTime(0, 0);
  static readonly NOON = new LocalTime(12, 0);

  private constructor(readonly hour: number, readonly minute: number) {}

  static of(hour: number, minute = 0): LocalTime {
    return new LocalTime(hour, minute);
  }

  private get minuteOfDay(): number {
    return this.hour * 60 + this.minute;
  }

  compareTo(other: LocalTime): number {
    return this.minuteOfDay - other.minuteOfDay;
  }

  isBefore(other: LocalTime): boolean {
    return this.compareTo(other) < 0;
  }

  isAfter(other: LocalTime): boolean {
    return this.compareTo(other) > 0;
  }

  equals(other: unknown): boolean {
    return other instanceof LocalTime && this.compareTo(other) === 0;
  }

  toString(): string {
    return `${pad(this.hour)}:${pad(this.minute)}`;
  }
}

// Odpowiednik java.time.LocalDate.
export class LocalDate {
  private constructor(private readonly epochDay: number) {}

  static of(year: number, month: number, day: number): LocalDate {
    return new LocalDate(Date.UTC(year, month - 1, day) / MILLIS_PER_DAY);
  }

  get dayOfWeek(): DayOfWeek {
    return dayOfWeekOf(this.epochDay * MILLIS_PER_DAY);
  }

  plusDays(days: number): LocalDate {
    return new LocalDate(this.epochDay + days);
  }

  atTime(time: LocalTime): LocalDateTime {
    return LocalDateTime.ofEpochMillis(
      this.epochDay * MILLIS_PER_DAY + time.hour * MILLIS_PER_HOUR + time.minute * MILLIS_PER_MINUTE,
    );
  }

  compareTo(other: LocalDate): number {
    return this.epochDay - other.epochDay;
  }

  isBefore(other: LocalDate): boolean {
    return this.compareTo(other) < 0;
  }

  isAfter(other: LocalDate): boolean {
    return this.compareTo(other) > 0;
  }

  equals(other: unknown): boolean {
    return other instanceof LocalDate && this.epochDay === other.epochDay;
  }

  toString(): string {
    return new Date(this.epochDay * MILLIS_PER_DAY).toISOString().slice(0, 10);
  }
}

// Odpowiednik java.time.LocalDateTime (dokładność do sekundy).
export class LocalDateTime {
  private constructor(private readonly epochMillis: number) {}

  static of(year: number, month: number, day: number, hour: number, minute: number, second = 0): LocalDateTime {
    return new LocalDateTime(Date.UTC(year, month - 1, day, hour, minute, second));
  }

  // Format ISO bez strefy, np. "2026-03-10T20:00" albo "2026-03-10T20:00:30".
  static parse(text: string): LocalDateTime {
    return new LocalDateTime(Date.parse(`${text}Z`));
  }

  static ofEpochMillis(epochMillis: number): LocalDateTime {
    return new LocalDateTime(epochMillis);
  }

  // Odpowiednik LocalDateTime.now(clock).
  static now(clock: Clock = systemClock): LocalDateTime {
    return clock.now();
  }

  private get date(): Date {
    return new Date(this.epochMillis);
  }

  get hour(): number {
    return this.date.getUTCHours();
  }

  get minute(): number {
    return this.date.getUTCMinutes();
  }

  get dayOfWeek(): DayOfWeek {
    return dayOfWeekOf(this.epochMillis);
  }

  toLocalDate(): LocalDate {
    const d = this.date;
    return LocalDate.of(d.getUTCFullYear(), d.getUTCMonth() + 1, d.getUTCDate());
  }

  toLocalTime(): LocalTime {
    return LocalTime.of(this.hour, this.minute);
  }

  toEpochMillis(): number {
    return this.epochMillis;
  }

  plusMinutes(minutes: number): LocalDateTime {
    return new LocalDateTime(this.epochMillis + minutes * MILLIS_PER_MINUTE);
  }

  minusMinutes(minutes: number): LocalDateTime {
    return this.plusMinutes(-minutes);
  }

  plusHours(hours: number): LocalDateTime {
    return new LocalDateTime(this.epochMillis + hours * MILLIS_PER_HOUR);
  }

  minusHours(hours: number): LocalDateTime {
    return this.plusHours(-hours);
  }

  plusDays(days: number): LocalDateTime {
    return new LocalDateTime(this.epochMillis + days * MILLIS_PER_DAY);
  }

  compareTo(other: LocalDateTime): number {
    return this.epochMillis - other.epochMillis;
  }

  isBefore(other: LocalDateTime): boolean {
    return this.compareTo(other) < 0;
  }

  isAfter(other: LocalDateTime): boolean {
    return this.compareTo(other) > 0;
  }

  equals(other: unknown): boolean {
    return other instanceof LocalDateTime && this.epochMillis === other.epochMillis;
  }

  // Jak LocalDateTime.toString(): sekundy tylko gdy różne od zera.
  toString(): string {
    const d = this.date;
    const seconds = d.getUTCSeconds();
    return `${d.getUTCFullYear()}-${pad(d.getUTCMonth() + 1)}-${pad(d.getUTCDate())}`
      + `T${pad(d.getUTCHours())}:${pad(d.getUTCMinutes())}${seconds === 0 ? '' : `:${pad(seconds)}`}`;
  }
}

// Odpowiednik java.time.Duration (dokładność do milisekundy).
export class Duration {
  static readonly ZERO = new Duration(0);

  private constructor(private readonly millis: number) {}

  static between(start: LocalDateTime, end: LocalDateTime): Duration {
    return new Duration(end.toEpochMillis() - start.toEpochMillis());
  }

  static ofMinutes(minutes: number): Duration {
    return new Duration(minutes * MILLIS_PER_MINUTE);
  }

  static ofHours(hours: number): Duration {
    return new Duration(hours * MILLIS_PER_HOUR);
  }

  static ofSeconds(seconds: number): Duration {
    return new Duration(seconds * 1000);
  }

  // Jak w Javie: obcięcie w stronę zera.
  toHours(): number {
    return Math.trunc(this.millis / MILLIS_PER_HOUR);
  }

  toMinutes(): number {
    return Math.trunc(this.millis / MILLIS_PER_MINUTE);
  }

  toSeconds(): number {
    return Math.trunc(this.millis / 1000);
  }

  isNegative(): boolean {
    return this.millis < 0;
  }

  compareTo(other: Duration): number {
    return this.millis - other.millis;
  }

  equals(other: unknown): boolean {
    return other instanceof Duration && this.millis === other.millis;
  }
}

// Odpowiednik java.time.Clock: źródło bieżącego czasu wstrzykiwane jako zależność.
export interface Clock {
  now(): LocalDateTime;
}

export const systemClock: Clock = {
  // Czas lokalny maszyny zapisany jako "ścienny" LocalDateTime.
  now: () => {
    const d = new Date();
    return LocalDateTime.of(d.getFullYear(), d.getMonth() + 1, d.getDate(), d.getHours(), d.getMinutes(), d.getSeconds());
  },
};

// Odpowiednik Clock.fixed(...).
export function fixedClock(at: LocalDateTime): Clock {
  return { now: () => at };
}
