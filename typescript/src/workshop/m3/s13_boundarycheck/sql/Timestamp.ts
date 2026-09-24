import { LocalDateTime } from '../../../shared/time.js';

/**
 * Świat zewnętrzny sceny (stabilny): typ sterownika bazy danych - odpowiednik
 * znacznika czasu SQL. Import czegokolwiek z katalogu sql to "technologia".
 */
export class Timestamp {
  private constructor(private readonly epochMillis: number) {}

  static valueOf(dateTime: LocalDateTime): Timestamp {
    return new Timestamp(dateTime.toEpochMillis());
  }

  toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofEpochMillis(this.epochMillis);
  }

  /** Format SQL: "2026-10-02 20:00:00.0" (część ułamkowa sekundy bez zer końcowych). */
  toString(): string {
    const iso = new Date(this.epochMillis).toISOString();
    const millis = this.epochMillis % 1000;
    const fraction = millis === 0 ? '0' : String(millis).padStart(3, '0').replace(/0+$/, '');
    return `${iso.slice(0, 10)} ${iso.slice(11, 19)}.${fraction}`;
  }
}
