import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m6/s15_command/start/CashierConsole.js';
import * as step1 from '../../../../src/workshop/m6/s15_command/step1/CashierConsole.js';
import * as step2 from '../../../../src/workshop/m6/s15_command/step2/CashierConsole.js';
import * as step3 from '../../../../src/workshop/m6/s15_command/step3/CashierConsole.js';
import { Scene } from '../../support/scene.js';

interface Console {
  handle(line: string): string;
}

function session(console: () => Console): (lines: readonly string[]) => string {
  return (lines) => {
    const cashier = console();
    return lines.map((line) => cashier.handle(line)).join('\n');
  };
}

/** Sesja kasjera: ta sama sekwencja poleceń daje te same odpowiedzi i ten sam stan kasy. */
describe('S15EquivalenceTest', () => {
  describe('everyStepHandlesTheSessionTheSame', () => {
    Scene.variants<readonly string[], string>()
      .variant('start', session(() => new start.CashierConsole()))
      .variant('step1', session(() => new step1.CashierConsole()))
      .variant('step2', session(() => new step2.CashierConsole()))
      .variant('step3', session(() => new step3.CashierConsole()))
      .expect('sprzedaż, zwrot, raport', ['SELL 2 Diuna', 'sell 1 Kraina Lodu', 'REFUND Diuna', 'REPORT'], [
        'Sprzedano 2 x Diuna = 80.00',
        'Sprzedano 1 x Kraina Lodu = 32.00',
        'Zwrot 1 x Diuna = 40.00',
        'Kasa: 72.00, biletow: 2',
      ].join('\n'))
      .expect('błędy nie zmieniają stanu', ['SELL Diuna', 'SELL 2 Batman', 'REFUND Amator', 'PRINT', '  report  '], [
        'Blad: SELL <liczba> <tytul>',
        'Blad: nieznany film Batman',
        'Blad: brak biletow do zwrotu',
        'Nieznana komenda: PRINT',
        'Kasa: 0.00, biletow: 0',
      ].join('\n'))
      .tests();
  });
});
