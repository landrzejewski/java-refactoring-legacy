import { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';

/** Wiersz raportu. Nazwy pól trafiają do nagłówka CSV. */
export class Line {
  constructor(readonly t: string, readonly n: number, readonly d: Money) {}
}

/**
 * Krok 2: Rename metody calc2 -> revenueCsv. IDE poprawiło wywołania w kodzie, ale nie konfigurację
 * ReportJob (tekst, a w produkcji plik na serwerze) - test zadania to wyłapał.
 * Strategia migracji: stara nazwa zostaje jako przestarzały delegat, dopóki konfiguracja jej używa.
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
