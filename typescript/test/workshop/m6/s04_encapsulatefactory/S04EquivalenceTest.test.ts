import { describe } from 'vitest';

import { SeatSale } from '../../../../src/workshop/m6/s04_encapsulatefactory/SeatSale.js';
import * as start from '../../../../src/workshop/m6/s04_encapsulatefactory/start/BoxOffice.js';
import * as step1 from '../../../../src/workshop/m6/s04_encapsulatefactory/step1/BoxOffice.js';
import * as step2 from '../../../../src/workshop/m6/s04_encapsulatefactory/step2/BoxOffice.js';
import * as step3 from '../../../../src/workshop/m6/s04_encapsulatefactory/step3/BoxOffice.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { Scene } from '../../support/scene.js';

interface Described {
  describe(): string;
}

interface Office {
  sell(title: string, base: Money, row: number): Described;
  sellAll(title: string, base: Money, rows: readonly number[]): Described[];
}

function play(office: Office, s: SeatSale): string {
  return `${office.sell(s.title, s.base, s.rows[0]!).describe()} | `
    + office.sellAll(s.title, s.base, s.rows).map((t) => t.describe()).join('; ');
}

/** Sprzedaż pojedyncza i grupowa daje te same bilety w każdym kroku. */
describe('S04EquivalenceTest', () => {
  describe('everyStepSellsTheSameTickets', () => {
    Scene.variants<SeatSale, string>()
      .variant('start', (s) => play(new start.BoxOffice(), s))
      .variant('step1', (s) => play(new step1.BoxOffice(), s))
      .variant('step2', (s) => play(new step2.BoxOffice(), s))
      .variant('step3', (s) => play(new step3.BoxOffice(), s))
      .expect('IMAX, granica VIP na rzędzie 10',
        new SeatSale('Diuna', Money.of('40.00'), [9, 10, 12]),
        'Diuna r9 40.00 | Diuna r9 40.00; Diuna r10 VIP 50.00; Diuna r12 VIP 50.00')
      .expect('2D, pierwsze miejsce VIP',
        new SeatSale('Amator', Money.of('25.00'), [11, 1]),
        'Amator r11 VIP 35.00 | Amator r11 VIP 35.00; Amator r1 25.00')
      .tests();
  });
});
