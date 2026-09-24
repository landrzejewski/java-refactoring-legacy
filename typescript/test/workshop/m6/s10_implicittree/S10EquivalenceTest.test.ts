import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m6/s10_implicittree/start/BarMenu.js';
import * as step1 from '../../../../src/workshop/m6/s10_implicittree/step1/BarMenu.js';
import * as step2 from '../../../../src/workshop/m6/s10_implicittree/step2/BarMenu.js';
import * as step3 from '../../../../src/workshop/m6/s10_implicittree/step3/BarMenu.js';
import { Scene } from '../../support/scene.js';
import { safe } from './safe.js';

interface Menu {
  price(combo: readonly unknown[]): { toString(): string };
  render(combo: readonly unknown[]): string;
}

function adapt(menu: Menu): (definition: readonly unknown[]) => string {
  return safe((d) => menu.price(d), (d) => menu.render(d));
}

/** Niezależne oczekiwania (policzone ręcznie) dla ceny i wydruku zestawów. */
describe('S10EquivalenceTest', () => {
  describe('everyStepPricesAndRendersCombosTheSame', () => {
    Scene.variants<readonly unknown[], string>()
      .variant('start', adapt(new start.BarMenu()))
      .variant('step1', adapt(new step1.BarMenu()))
      .variant('step2', adapt(new step2.BarMenu()))
      .variant('step3', adapt(new step3.BarMenu()))
      .expect('zestaw z podzestawem', ['Zestaw Duo', 'Popcorn L=18.00',
        ['Napoje', 'Cola 0.5=9.00', 'Cola 0.5=9.00'], 'Nachos=14.00'], `50.00
Zestaw Duo 50.00
  Popcorn L 18.00
  Napoje 18.00
    Cola 0.5 9.00
    Cola 0.5 9.00
  Nachos 14.00
`)
      .expect('pusty zestaw', ['Pusty'], '0.00\nPusty 0.00\n')
      .expect('brak nazwy', [], 'ERROR combo needs a name')
      .expect('element nieobsługiwany', ['Zestaw', 'Cola=9.00', 5], 'ERROR unsupported element: 5')
      .expect('produkt bez ceny', ['Zestaw', 'Cola'], 'ERROR product needs a price: Cola')
      .tests();
  });
});
