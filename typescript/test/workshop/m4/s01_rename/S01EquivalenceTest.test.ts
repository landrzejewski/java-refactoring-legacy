import { describe } from 'vitest';

import { Sale } from '../../../../src/workshop/m4/s01_rename/Sale.js';
import * as start from '../../../../src/workshop/m4/s01_rename/start/SalesReport.js';
import * as startJob from '../../../../src/workshop/m4/s01_rename/start/ReportJob.js';
import * as step1 from '../../../../src/workshop/m4/s01_rename/step1/SalesReport.js';
import * as step1Job from '../../../../src/workshop/m4/s01_rename/step1/ReportJob.js';
import * as step2 from '../../../../src/workshop/m4/s01_rename/step2/SalesReport.js';
import * as step2Job from '../../../../src/workshop/m4/s01_rename/step2/ReportJob.js';
import * as step3 from '../../../../src/workshop/m4/s01_rename/step3/SalesReport.js';
import * as step3Job from '../../../../src/workshop/m4/s01_rename/step3/ReportJob.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { Scene } from '../../support/scene.js';

const SALES: readonly Sale[] = [
  new Sale('Diuna', 2, Money.of('80.00'), true),
  new Sale('Amator', 1, Money.of('25.00'), false),
  new Sale('Diuna', 1, Money.of('40.00'), false),
  new Sale('Kraina Lodu', 3, Money.of('96.00'), true),
];

/**
 * Test równoważności: ten sam CSV z wywołania w kodzie i z zadania uruchamianego z konfiguracji.
 * Drugi test to ten, który "łapie" Rename: IDE przemianowuje kod, ale nie tekst konfiguracji.
 */
describe('S01EquivalenceTest', () => {
  describe('everyStepPrintsTheSameCsv', () => {
    Scene.variants<readonly Sale[], string>()
      .variant('start', (s) => new start.SalesReport().calc2(s, false))
      .variant('step1', (s) => new step1.SalesReport().calc2(s, false))
      .variant('step2', (s) => new step2.SalesReport().revenueCsv(s, false))
      .variant('step3', (s) => new step3.SalesReport().revenueCsv(s, false))
      .expect('wszystkie sprzedaże, nagłówek t;n;d', SALES, `t;n;d
Amator;1;25.00
Diuna;3;120.00
Kraina Lodu;3;96.00
`)
      .expect('brak sprzedaży - sam nagłówek', [], 't;n;d\n')
      .tests();
  });

  describe('everyStepRunsTheConfiguredJob', () => {
    Scene.variants<readonly Sale[], string>()
      .variant('start', (s) => new startJob.ReportJob().run(s))
      .variant('step1', (s) => new step1Job.ReportJob().run(s))
      .variant('step2', (s) => new step2Job.ReportJob().run(s))
      .variant('step3', (s) => new step3Job.ReportJob().run(s))
      .expect('konfiguracja: report.method=calc2, tylko online', SALES, `t;n;d
Diuna;2;80.00
Kraina Lodu;3;96.00
`)
      .tests();
  });
});
