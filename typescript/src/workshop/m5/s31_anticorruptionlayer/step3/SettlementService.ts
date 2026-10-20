import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';
import type { LocalDate } from '../../../shared/time.js';
import type { FilmLicenses } from './FilmLicenses.js';

/**
 * Krok 3: serwis domeny zależy tylko od portu FilmLicenses i mówi wyłącznie językiem kina.
 * Żaden typ dystrybutora nie przekracza warstwy antykorupcyjnej (DistributorAcl).
 */
export class SettlementService {
  private readonly licenses: FilmLicenses;

  constructor(licenses: FilmLicenses) {
    this.licenses = requireNonNull(licenses, 'licenses');
  }

  canScreen(filmCode: string, day: LocalDate): boolean {
    return this.licenses.find(filmCode)?.allowsScreeningOn(day) ?? false;
  }

  settle(filmCode: string, day: LocalDate, revenue: Money): string {
    const license = this.licenses.find(filmCode);
    if (license === undefined) {
      return `BRAK LICENCJI ${filmCode}`;
    }
    if (!license.active) {
      return `LICENCJA ZAWIESZONA ${filmCode}`;
    }
    if (!license.covers(day)) {
      return `LICENCJA NIEWAZNA ${filmCode} w dniu ${day.toString()}`;
    }
    return `ROZLICZENIE ${license.title} ${day.toString()}: przychod ${revenue.toString()}, udzial `
      + `${license.sharePercent}%, minimum ${license.minimumGuarantee.toString()}`
      + `, dla dystrybutora ${license.dueFor(revenue).toString()}`;
  }
}
