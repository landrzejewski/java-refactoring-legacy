import type { Sale } from '../Sale.js';
import { AccountingSection } from './AccountingSection.js';
import { MarketingSection } from './MarketingSection.js';

/**
 * Krok 3 (rozwiązanie): Extract Class - każda sekcja w klasie swojego aktora.
 * DailyReport tylko składa dokument (koordynuje, nie zna polityk). Zmiana definicji
 * hitu dotyka wyłącznie `MarketingSection`.
 */
export class DailyReport {
  private readonly accounting = new AccountingSection();
  private readonly marketing = new MarketingSection();

  render(sales: readonly Sale[]): string {
    return this.accounting.render(sales) + this.marketing.render(sales);
  }
}
