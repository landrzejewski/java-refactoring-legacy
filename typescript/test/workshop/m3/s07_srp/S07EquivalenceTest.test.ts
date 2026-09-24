import { Decimal } from 'decimal.js';
import { describe } from 'vitest';

import { Sale } from '../../../../src/workshop/m3/s07_srp/Sale.js';
import * as start from '../../../../src/workshop/m3/s07_srp/start/DailyReport.js';
import * as step1 from '../../../../src/workshop/m3/s07_srp/step1/DailyReport.js';
import * as step2 from '../../../../src/workshop/m3/s07_srp/step2/DailyReport.js';
import * as step3 from '../../../../src/workshop/m3/s07_srp/step3/DailyReport.js';
import { Scene } from '../../support/scene.js';

function sale(title: string, tickets: number, ticketRevenue: string, barRevenue: string): Sale {
  return new Sale(title, tickets, new Decimal(ticketRevenue), new Decimal(barRevenue));
}

/** Podział raportu według aktorów nie zmienia ani jednego znaku dokumentu. */
describe('S07EquivalenceTest', () => {
  describe('everyStepRendersTheSameReport', () => {
    Scene.variants<readonly Sale[], string>()
      .variant('start', (sales) => new start.DailyReport().render(sales))
      .variant('step1', (sales) => new step1.DailyReport().render(sales))
      .variant('step2', (sales) => new step2.DailyReport().render(sales))
      .variant('step3', (sales) => new step3.DailyReport().render(sales))
      .expect('trzy seanse, Diuna dwa razy',
        [sale('Diuna', 2, '80.00', '18.00'),
          sale('Amator', 3, '75.00', '12.00'),
          sale('Diuna', 1, '40.00', '0.00')],
        `KSIEGOWOSC
Bilety brutto 195.00, netto 180.56
Bar brutto 30.00, netto 24.39
Razem brutto 225.00
MARKETING
Hit dnia: Diuna (138.00)
Sprzedanych biletow: 6
`)
      .expect('remis - wygrywa pierwszy alfabetycznie',
        [sale('Kraina Lodu', 1, '20.00', '5.00'), sale('Amator', 1, '25.00', '0.00')],
        `KSIEGOWOSC
Bilety brutto 45.00, netto 41.67
Bar brutto 5.00, netto 4.07
Razem brutto 50.00
MARKETING
Hit dnia: Amator (25.00)
Sprzedanych biletow: 2
`)
      .expect('dzien bez sprzedazy', [],
        `KSIEGOWOSC
Bilety brutto 0.00, netto 0.00
Bar brutto 0.00, netto 0.00
Razem brutto 0.00
MARKETING
Hit dnia: brak
Sprzedanych biletow: 0
`)
      .tests();
  });
});
