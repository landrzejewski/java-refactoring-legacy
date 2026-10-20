import { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';

/** Wiersz raportu. Nazwy pól trafiają do nagłówka CSV. */
export class Line {
  constructor(readonly t: string, readonly n: number, readonly d: Money) {}
}

/**
 * Start: raport sprzedaży dla dystrybutora (CSV). Nazwy nic nie mówią: calc2, s, flag, m, x, l,
 * a pola wiersza to t, n, d. Dwie nazwy żyją też POZA kodem:
 * "calc2" w konfiguracji ReportJob (wywołanie po nazwie) i t/n/d w nagłówku CSV (Object.keys wiersza).
 */
export class SalesReport {
  /** Wywoływana także po nazwie przez ReportJob - nazwa metody jest w konfiguracji. */
  calc2(s: readonly Sale[], flag: boolean): string {
    const m = new Map<string, Line>();
    for (const x of s) {
      if (flag && !x.online) {
        continue;
      }
      const l = m.get(x.title);
      if (l === undefined) {
        m.set(x.title, new Line(x.title, x.tickets, x.amount));
      } else {
        m.set(x.title, new Line(x.title, l.n + x.tickets, l.d.plus(x.amount)));
      }
    }
    let b = header() + '\n';
    for (const l of sortedByKey(m)) {
      b += row(l) + '\n';
    }
    return b;
  }
}

function header(): string {
  return Object.keys(new Line('', 0, Money.ZERO)).join(';');
}

function row(l: Line): string {
  const v: string[] = [];
  for (const c of Object.values(l)) {
    v.push(String(c));
  }
  return v.join(';');
}

// Odpowiednik TreeMap: wartości w kolejności kluczy (porównanie jak String.compareTo).
function sortedByKey<V>(map: ReadonlyMap<string, V>): V[] {
  return [...map].sort(([a], [b]) => (a < b ? -1 : a > b ? 1 : 0)).map(([, value]) => value);
}
