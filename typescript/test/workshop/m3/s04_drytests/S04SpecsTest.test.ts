import { describe, expect, it } from 'vitest';

import * as start from '../../../../src/workshop/m3/s04_drytests/start/TicketPriceSpecs.js';
import * as step1 from '../../../../src/workshop/m3/s04_drytests/step1/TicketPriceSpecs.js';
import * as step2 from '../../../../src/workshop/m3/s04_drytests/step2/TicketPriceSpecs.js';
import { Tariff } from '../../../../src/workshop/m3/s04_drytests/Tariff.js';
import { TicketPrice } from '../../../../src/workshop/m3/s04_drytests/TicketPrice.js';
import { Scene } from '../../support/scene.js';

/**
 * Dla poprawnej taryfy każda wersja specyfikacji jest zielona. Różnica wychodzi dopiero,
 * gdy taryfa ma błąd: krok 1 (i start, z tą samą wyrocznią) go nie widzi - pułapka;
 * krok 2 go łapie. Pułapkę sprawdzamy na kopii z kroku 1, bo start jest edytowany na żywo.
 */
describe('S04SpecsTest', () => {
  const BUGGY = Tariff.standard().withDiscount('STUDENT', 20);

  describe('everyVersionAcceptsCorrectTariff', () => {
    Scene.variants<Tariff, string[]>()
      .variant('start', (t) => new start.TicketPriceSpecs().run(new TicketPrice(t)))
      .variant('step1', (t) => new step1.TicketPriceSpecs().run(new TicketPrice(t)))
      .variant('step2', (t) => new step2.TicketPriceSpecs().run(new TicketPrice(t)))
      .expect('taryfa zgodna z regulaminem', Tariff.standard(), [])
      .tests();
  });

  it('step1IsReadableButStillBlindToTheBug', () => {
    expect(new step1.TicketPriceSpecs().run(new TicketPrice(BUGGY))).toEqual([]);
  });

  it('step2CatchesBrokenStudentDiscount', () => {
    expect(new step2.TicketPriceSpecs().run(new TicketPrice(BUGGY)))
      .toEqual(['student na porannym 3D: oczekiwano 19.00, jest 20.60']);
  });
});
