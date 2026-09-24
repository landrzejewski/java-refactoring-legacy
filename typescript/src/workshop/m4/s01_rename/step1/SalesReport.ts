import { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';

/** Wiersz raportu. Nazwy pól trafiają do nagłówka CSV. */
export class Line {
  constructor(readonly t: string, readonly n: number, readonly d: Money) {}
}

/**
 * Krok 1: Rename zmiennych lokalnych i parametrów (s, flag, m, x, l, b, v, c).
 * Zasięg lokalny, brak użyć poza metodą - IDE robi to w pełni bezpiecznie.
 */
export class SalesReport {
  /** Wywoływana także po nazwie przez ReportJob - nazwa metody jest w konfiguracji. */
  calc2(sales: readonly Sale[], onlineOnly: boolean): string {
    const linesByTitle = new Map<string, Line>();
    for (const sale of sales) {
      if (onlineOnly && !sale.online) {
        continue;
      }
      const previous = linesByTitle.get(sale.title);
      if (previous === undefined) {
        linesByTitle.set(sale.title, new Line(sale.title, sale.tickets, sale.amount));
      } else {
        linesByTitle.set(sale.title, new Line(sale.title,
          previous.n + sale.tickets, previous.d.plus(sale.amount)));
      }
    }
    let csv = header() + '\n';
    for (const line of sortedByKey(linesByTitle)) {
      csv += row(line) + '\n';
    }
    return csv;
  }
}

function header(): string {
  return Object.keys(new Line('', 0, Money.ZERO)).join(';');
}

function row(line: Line): string {
  const values: string[] = [];
  for (const component of Object.values(line)) {
    values.push(String(component));
  }
  return values.join(';');
}

// Odpowiednik TreeMap: wartości w kolejności kluczy (porównanie jak String.compareTo).
function sortedByKey<V>(map: ReadonlyMap<string, V>): V[] {
  return [...map].sort(([a], [b]) => (a < b ? -1 : a > b ? 1 : 0)).map(([, value]) => value);
}
