import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m5/s08_composition/start/SeatSelection.js';
import * as step1 from '../../../../src/workshop/m5/s08_composition/step1/SeatSelection.js';
import * as step2 from '../../../../src/workshop/m5/s08_composition/step2/SeatSelection.js';
import { Scene } from '../../support/scene.js';

/**
 * Test równoważności na pojedynczych kliknięciach (add), gdzie start działa poprawnie.
 * Hurtowe addAll() różni się celowo - patrz S08SolutionTest.
 */
/**
 * Start czyta rozmiar niezależnie od kształtu API: w start to pole `size` odziedziczone po Set,
 * a po `jump`/`next` fasada z kroków ma metodę `size()`. Dzięki temu test typuje się i działa
 * dla każdego wariantu w start (jak w Javie, gdzie size() jest w obu wersjach).
 */
function sizeOf(selection: object): number {
  const size = (selection as { size: number | (() => number) }).size;
  return typeof size === 'function' ? size.call(selection) : size;
}

describe('S08EquivalenceTest', () => {
  describe('everyStepCountsSingleClicksTheSameWay', () => {
    Scene.variants<readonly string[], string>()
      .variant('start', (seats) => {
        const selection = new start.SeatSelection();
        seats.forEach((seat) => selection.add(seat));
        return sizeOf(selection) + ' miejsc, ' + selection.clicks() + ' kliknięć';
      })
      .variant('step1', (seats) => {
        const selection = new step1.SeatSelection();
        seats.forEach((seat) => selection.add(seat));
        return selection.size() + ' miejsc, ' + selection.clicks() + ' kliknięć';
      })
      .variant('step2', (seats) => {
        const selection = new step2.SeatSelection();
        seats.forEach((seat) => selection.add(seat));
        return selection.size() + ' miejsc, ' + selection.clicks() + ' kliknięć';
      })
      .expect('dwa miejsca', ['H7', 'H8'], '2 miejsc, 2 kliknięć')
      .expect('ponowne kliknięcie tego samego miejsca', ['H7', 'H8', 'H7'], '2 miejsc, 3 kliknięć')
      .expect('nic nie wybrano', [], '0 miejsc, 0 kliknięć')
      .tests();
  });
});
