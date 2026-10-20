import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalDate } from '../../../../src/workshop/shared/time.js';
import { DistributorFeed } from '../../../../src/workshop/m5/s31_anticorruptionlayer/DistributorFeed.js';
import { DistributorRecord } from '../../../../src/workshop/m5/s31_anticorruptionlayer/DistributorRecord.js';
import * as start from '../../../../src/workshop/m5/s31_anticorruptionlayer/start/SettlementModule.js';
import * as step1 from '../../../../src/workshop/m5/s31_anticorruptionlayer/step1/SettlementModule.js';
import * as step2 from '../../../../src/workshop/m5/s31_anticorruptionlayer/step2/SettlementModule.js';
import * as step3 from '../../../../src/workshop/m5/s31_anticorruptionlayer/step3/SettlementModule.js';
import { Scene } from '../../support/scene.js';

const FEED = new DistributorFeed([
  new DistributorRecord('DIU', 'DIUNA', 'A', '20261001', '20261031', '50000', '45%'),
  new DistributorRecord('KRL', 'KRAINA LODU', 'A', '20261015', '20261115', '30000', ' 40 %'),
  new DistributorRecord('AMA', 'AMATOR', 'S', '20260101', '20261231', '10000', '50%'),
]);

interface Service {
  settle(filmCode: string, day: LocalDate, revenue: Money): string;
  canScreen(filmCode: string, day: LocalDate): boolean;
}

/** Odpowiednik LocalDate.parse("2026-10-20"). */
function isoDate(text: string): LocalDate {
  const [year, month, day] = text.split('-').map(Number);
  return LocalDate.of(year ?? 0, month ?? 0, day ?? 0);
}

/** Wiersz: "settle KOD DZIEN PRZYCHOD" albo "can KOD DZIEN". */
function run(service: () => Service): (line: string) => string {
  return (line) => {
    const [command = '', filmCode = '', day = '', revenue = ''] = line.split(' ');
    return command === 'can'
      ? String(service().canScreen(filmCode, isoDate(day)))
      : service().settle(filmCode, isoDate(day), Money.of(revenue));
  };
}

/**
 * Rozliczenia i dopuszczenie do emisji na trzech rekordach dystrybutora: udział vs minimalna
 * gwarancja, zaokrąglenie, granice okresu licencji (włącznie), "45%" i " 40 %", licencja
 * zawieszona i nieznany film.
 */
describe('S31EquivalenceTest', () => {
  describe('everyStepSettlesTheSame', () => {
    Scene.variants<string, string>()
      .variant('start', run(() => start.SettlementModule.settlementService(FEED)))
      .variant('step1', run(() => step1.SettlementModule.settlementService(FEED)))
      .variant('step2', run(() => step2.SettlementModule.settlementService(FEED)))
      .variant('step3', run(() => step3.SettlementModule.settlementService(FEED)))
      .expect('udział powyżej minimum', 'settle DIU 2026-10-20 2000.00',
        'ROZLICZENIE Diuna 2026-10-20: przychod 2000.00, udzial 45%, minimum 500.00, dla dystrybutora 900.00')
      .expect('minimalna gwarancja', 'settle DIU 2026-10-20 600.00',
        'ROZLICZENIE Diuna 2026-10-20: przychod 600.00, udzial 45%, minimum 500.00, dla dystrybutora 500.00')
      .expect('udział ze spacjami i zaokrąglenie', 'settle KRL 2026-10-20 1234.57',
        'ROZLICZENIE Kraina lodu 2026-10-20: przychod 1234.57, udzial 40%, minimum 300.00, '
          + 'dla dystrybutora 493.83')
      .expect('pierwszy dzień licencji', 'settle DIU 2026-10-01 2000.00',
        'ROZLICZENIE Diuna 2026-10-01: przychod 2000.00, udzial 45%, minimum 500.00, dla dystrybutora 900.00')
      .expect('ostatni dzień licencji', 'settle DIU 2026-10-31 2000.00',
        'ROZLICZENIE Diuna 2026-10-31: przychod 2000.00, udzial 45%, minimum 500.00, dla dystrybutora 900.00')
      .expect('dzień po licencji', 'settle DIU 2026-11-01 2000.00', 'LICENCJA NIEWAZNA DIU w dniu 2026-11-01')
      .expect('dzień przed licencją', 'settle DIU 2026-09-30 2000.00', 'LICENCJA NIEWAZNA DIU w dniu 2026-09-30')
      .expect('licencja zawieszona', 'settle AMA 2026-10-20 100.00', 'LICENCJA ZAWIESZONA AMA')
      .expect('nieznany film', 'settle XYZ 2026-10-20 100.00', 'BRAK LICENCJI XYZ')
      .expect('emisja w okresie', 'can DIU 2026-10-20', 'true')
      .expect('emisja po okresie', 'can DIU 2026-11-01', 'false')
      .expect('emisja przed okresem', 'can KRL 2026-10-14', 'false')
      .expect('emisja w ostatnim dniu', 'can KRL 2026-11-15', 'true')
      .expect('emisja zawieszonej', 'can AMA 2026-10-20', 'false')
      .expect('emisja nieznanego', 'can XYZ 2026-10-20', 'false')
      .tests();
  });
});
