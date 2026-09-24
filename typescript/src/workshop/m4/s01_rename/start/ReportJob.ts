import { IllegalStateError } from '../../../../shared/errors.js';
import type { Sale } from '../Sale.js';
import { SalesReport } from './SalesReport.js';

/**
 * Nocne zadanie raportu. Metodę wybiera konfiguracja, a nie kod - IDE nie widzi tego użycia.
 * W produkcji CONFIG to plik report.properties na serwerze, poza repozytorium.
 */
export class ReportJob {
  static readonly CONFIG = `
report.method=calc2
report.onlineOnly=true
`;

  private readonly report = new SalesReport();

  run(sales: readonly Sale[]): string {
    const config = new Map(ReportJob.CONFIG.trim().split('\n')
      .map((line): [string, string] => {
        const [key = '', value = ''] = line.split('=');
        return [key.trim(), value.trim()];
      }));
    const methodName = config.get('report.method') ?? '';
    // Wywołanie po nazwie z tekstu - odpowiednik refleksji (getMethod + invoke).
    const method: unknown = (this.report as unknown as Record<string, unknown>)[methodName];
    if (typeof method !== 'function') {
      throw new IllegalStateError(`Zadanie raportu nie działa: brak metody ${methodName}`);
    }
    const onlineOnly = config.get('report.onlineOnly')?.toLowerCase() === 'true';
    return method.call(this.report, sales, onlineOnly) as string;
  }
}
