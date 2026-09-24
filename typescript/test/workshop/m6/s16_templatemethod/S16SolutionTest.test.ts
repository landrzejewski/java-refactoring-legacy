import { describe, expect, it } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalTime } from '../../../../src/workshop/shared/time.js';
import { Sale } from '../../../../src/workshop/m6/s16_templatemethod/Sale.js';
import { CsvSalesReport } from '../../../../src/workshop/m6/s16_templatemethod/step2/CsvSalesReport.js';
import { HtmlSalesReport } from '../../../../src/workshop/m6/s16_templatemethod/step2/HtmlSalesReport.js';
import { SalesReport } from '../../../../src/workshop/m6/s16_templatemethod/step2/SalesReport.js';

/** Szkielet jest chroniony ("final"), a nowy format to tylko trzy metody. */
describe('S16SolutionTest', () => {
  // TypeScript nie ma final: sprawdzamy, że podklasy nie mają własnego render(),
  // a podklasa, która próbuje go nadpisać, jest odrzucana przez konstruktor bazy.
  it('templateMethodIsFinal', () => {
    expect(Object.hasOwn(CsvSalesReport.prototype, 'render')).toBe(false);
    expect(Object.hasOwn(HtmlSalesReport.prototype, 'render')).toBe(false);
    class Overriding extends SalesReport {
      override render(): string {
        return 'inny szkielet';
      }

      protected override header(): string {
        return '';
      }

      protected override row(): string {
        return '';
      }

      protected override footer(): string {
        return '';
      }
    }
    expect(() => new Overriding()).toThrow(IllegalStateError);
  });

  it('newFormatReusesTheSkeleton', () => {
    const markdown = new (class extends SalesReport {
      protected override header(): string {
        return '| godzina | film |\n';
      }

      protected override row(sale: Sale): string {
        return `| ${sale.time.toString()} | ${sale.title} |\n`;
      }

      protected override footer(_tickets: number, total: Money): string {
        return `Razem: ${total.toString()}\n`;
      }
    })();
    expect(markdown.render([new Sale(LocalTime.of(18, 0), 'Diuna', 1, Money.of('40.00'))]))
      .toBe('| godzina | film |\n| 18:00 | Diuna |\nRazem: 40.00\n');
  });
});
