import { IllegalStateError } from '../../../../shared/errors.js';
import type { Hall } from '../Hall.js';

/**
 * Start: złożoność wprowadzona. Rodzaj miejsc wybierany stringiem i wywołaniem
 * dynamicznym ("all" -> allRows, "vip" -> vipRows), wolne miejsca liczone wyrażeniem
 * regularnym z nazwaną grupą. Działa, ale IDE widzi metody wierszy jako nieużywane,
 * literówka w "vip" wybucha dopiero w runtime, a przepływ jest ukryty.
 */
export class SeatCounter {
  private static readonly FREE = /(?<seat>\.)/g;

  summary(hall: Hall): string {
    return `wolne: ${this.free(hall, 'all')}, wolne VIP: ${this.free(hall, 'vip')}`;
  }

  free(hall: Hall, kind: string): number {
    const rows: unknown = Reflect.get(this, `${kind}Rows`);
    if (typeof rows !== 'function') {
      throw new IllegalStateError(`nieznany rodzaj miejsc: ${kind}`);
    }
    const selected = (rows as (hall: Hall) => Iterable<string>).call(this, hall);
    return [...selected]
      .flatMap((row) => [...row.matchAll(SeatCounter.FREE)].map((m) => m.groups?.['seat']))
      .length;
  }

  // wołane dynamicznie przez free()
  private allRows(hall: Hall): Iterable<string> {
    return hall.rows;
  }

  // wołane dynamicznie przez free()
  private vipRows(hall: Hall): Iterable<string> {
    return Array.from({ length: hall.rows.length }, (_, i) => i + 1)
      .filter((n) => n >= hall.vipFromRow)
      .map((n) => hall.rows[n - 1]!);
  }
}
