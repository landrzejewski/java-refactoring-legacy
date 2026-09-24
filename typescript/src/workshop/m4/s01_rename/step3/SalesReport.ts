import type { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';

/** Nagłówek uzgodniony z dystrybutorem - kontrakt zewnętrzny, NIE nazwy pól w kodzie. */
export const CSV_HEADER = 't;n;d';

export class Line {
  constructor(readonly title: string, readonly tickets: number, readonly revenue: Money) {}
}

/**
 * Krok 3: Rename pól wiersza t, n, d -> title, tickets, revenue.
 * Nagłówek CSV był wyliczany z nazw pól (Object.keys), więc sam Rename zmieniłby plik
 * dla dystrybutora. Dlatego odcinamy kontrakt zewnętrzny od nazw w kodzie:
 * jawny nagłówek i jawny wiersz.
 */
export class SalesReport {
  /**
   * Stara nazwa z konfiguracji report.properties (report.method=calc2).
   * Usunąć dopiero, gdy żaden serwer nie ma jej w konfiguracji.
   *
   * @deprecated użyj {@link revenueCsv}
   */
  calc2(sales: readonly Sale[], onlineOnly: boolean): string {
    return this.revenueCsv(sales, onlineOnly);
  }

  revenueCsv(sales: readonly Sale[], onlineOnly: boolean): string {
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
          previous.tickets + sale.tickets, previous.revenue.plus(sale.amount)));
      }
    }
    let csv = CSV_HEADER + '\n';
    for (const line of sortedByKey(linesByTitle)) {
      csv += row(line) + '\n';
    }
    return csv;
  }
}

function row(line: Line): string {
  return line.title + ';' + line.tickets + ';' + line.revenue.toString();
}

// Odpowiednik TreeMap: wartości w kolejności kluczy (porównanie jak String.compareTo).
function sortedByKey<V>(map: ReadonlyMap<string, V>): V[] {
  return [...map].sort(([a], [b]) => (a < b ? -1 : a > b ? 1 : 0)).map(([, value]) => value);
}
