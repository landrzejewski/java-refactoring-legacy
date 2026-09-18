import { ArithmeticError } from '../shared/errors.js';

// Pomocnicze odpowiedniki drobnych elementów JDK używanych w module 6.

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

// Odpowiednik exception.getClass().getSimpleName() — nazwa klasy błędu JS.
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
