import { ArithmeticError, IllegalArgumentError, UnsupportedOperationError } from '../shared/errors.js';

// Pomocnicze odpowiedniki drobnych elementów JDK używanych w module 5.

// Odpowiednik Math.addExact(long, long): JS nie ma typu long, więc granicą
// jest zakres bezpiecznych liczb całkowitych (Number.MAX_SAFE_INTEGER).
export function addExact(first: number, second: number): number {
  const result = first + second;
  if (!Number.isSafeInteger(first) || !Number.isSafeInteger(second) || !Number.isSafeInteger(result)) {
    throw new ArithmeticError('long overflow');
  }
  return result;
}

// Odpowiednik `value == null || value.isBlank()`.
export function isBlank(value: string | null | undefined): boolean {
  return value === null || value === undefined || value.trim() === '';
}

// Odpowiednik List.toString(): [a, b, c].
export function formatList(values: readonly unknown[]): string {
  return `[${values.map(String).join(', ')}]`;
}

// Odpowiednik exception.getClass().getSimpleName(): nazwa klasy błędu JS.
export function simpleName(error: unknown): string {
  if (error instanceof Error) {
    return error.name;
  }
  return typeof error;
}

// Minimalny odpowiednik java.time.Duration (tylko sekundy) z toString() w formacie ISO-8601.
export class Duration {
  private constructor(readonly seconds: number) {}

  static ofSeconds(seconds: number): Duration {
    return new Duration(seconds);
  }

  equals(other: Duration): boolean {
    return this.seconds === other.seconds;
  }

  toString(): string {
    if (this.seconds === 0) {
      return 'PT0S';
    }
    const hours = Math.trunc(this.seconds / 3600);
    const minutes = Math.trunc((this.seconds % 3600) / 60);
    const seconds = this.seconds % 60;
    return 'PT' + (hours !== 0 ? `${hours}H` : '') + (minutes !== 0 ? `${minutes}M` : '')
      + (seconds !== 0 ? `${seconds}S` : '');
  }
}

// Odpowiednik value.split(separator, 2): najwyżej dwie części, reszta w drugiej.
export function splitInTwo(value: string, separator: string): string[] {
  const index = value.indexOf(separator);
  return index < 0 ? [value] : [value.slice(0, index), value.slice(index + separator.length)];
}

// Odpowiednik Double.toString(double): liczba całkowita zawsze z częścią ułamkową (80 -> "80.0").
export function javaDouble(value: number): string {
  if (Number.isNaN(value)) {
    return 'NaN';
  }
  if (!Number.isFinite(value)) {
    return value > 0 ? 'Infinity' : '-Infinity';
  }
  if (Object.is(value, -0)) {
    return '-0.0';
  }
  const magnitude = Math.abs(value);
  if (magnitude === 0 || (magnitude >= 1e-3 && magnitude < 1e7)) {
    const text = String(value);
    return text.includes('.') ? text : `${text}.0`;
  }
  // notacja naukowa: Java "1.0E7", JavaScript "1e+7"
  const [mantissa = '', exponent = ''] = value.toExponential().split('e');
  return `${mantissa.includes('.') ? mantissa : `${mantissa}.0`}E${exponent.replace('+', '')}`;
}

// Odpowiednik java.lang.NumberFormatException. Jak w Javie jest podklasą
// IllegalArgumentError, więc `catch (IllegalArgumentException)` łapie też błędy parsowania.
export class NumberFormatError extends IllegalArgumentError {
  override name = 'NumberFormatError';
}

const JAVA_DOUBLE = /^[+-]?(NaN|Infinity|((\d+\.?\d*|\.\d+)([eE][+-]?\d+)?)[fFdD]?)$/;

// Odpowiednik Double.parseDouble(String). Number("") i Number(" ") dają 0, a Number("1_0") NaN,
// więc format sprawdzamy jawnie (jak Java: obcina białe znaki z obu stron).
export function parseDouble(text: string): number {
  const trimmed = text.trim();
  if (trimmed === '') {
    throw new NumberFormatError(text === '' ? 'empty String' : `For input string: "${text}"`);
  }
  if (!JAVA_DOUBLE.test(trimmed)) {
    throw new NumberFormatError(`For input string: "${text}"`);
  }
  return Number(trimmed.replace(/[fFdD]$/, ''));
}

// Odpowiednik Integer.parseInt(String): bez białych znaków, w zakresie int.
export function parseInteger(text: string): number {
  if (!/^[+-]?\d+$/.test(text)) {
    throw new NumberFormatError(`For input string: "${text}"`);
  }
  const value = Number(text);
  if (value < -2_147_483_648 || value > 2_147_483_647) {
    throw new NumberFormatError(`For input string: "${text}"`);
  }
  return value;
}

/**
 * Odpowiednik Set.copyOf(...): kopia wejścia, której metody modyfikujące
 * rzucają UnsupportedOperationError (Object.freeze nie blokuje Set.add).
 */
export function unmodifiableSet<T>(values: Iterable<T>): ReadonlySet<T> {
  const copy = new Set(values);
  const reject = (): never => {
    throw new UnsupportedOperationError();
  };
  return Object.freeze(Object.assign(copy, { add: reject, delete: reject, clear: reject }));
}

// Odpowiednik java.time.format.DateTimeParseException.
export class DateTimeParseError extends Error {
  override name = 'DateTimeParseError';
}

const MILLIS_PER_DAY = 86_400_000;

// Minimalny odpowiednik java.time.LocalDate (liczony w UTC, bez stref czasowych).
export class LocalDate {
  private constructor(private readonly epochDay: number) {}

  static of(year: number, month: number, day: number): LocalDate {
    return new LocalDate(Date.UTC(year, month - 1, day) / MILLIS_PER_DAY);
  }

  // Odpowiednik LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd")) w domyślnym
  // trybie SMART: dzień 29-31 spoza miesiąca jest przycinany do ostatniego dnia miesiąca.
  static parseBasicDate(text: string): LocalDate {
    const match = /^(\d{4})(\d{2})(\d{2})$/.exec(text);
    if (match === null) {
      throw new DateTimeParseError(`Text '${text}' could not be parsed at index 0`);
    }
    const year = Number(match[1]);
    const month = Number(match[2]);
    const day = Number(match[3]);
    if (month < 1 || month > 12 || day < 1 || day > 31) {
      throw new DateTimeParseError(`Text '${text}' could not be parsed`);
    }
    const lastDay = new Date(Date.UTC(year, month, 0)).getUTCDate();
    return LocalDate.of(year, month, Math.min(day, lastDay));
  }

  minusDays(days: number): LocalDate {
    return new LocalDate(this.epochDay - days);
  }

  isBefore(other: LocalDate): boolean {
    return this.epochDay < other.epochDay;
  }

  equals(other: unknown): boolean {
    return other instanceof LocalDate && this.epochDay === other.epochDay;
  }

  toString(): string {
    return new Date(this.epochDay * MILLIS_PER_DAY).toISOString().slice(0, 10);
  }
}
