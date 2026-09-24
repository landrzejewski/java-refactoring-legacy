import { describe, expect, it } from 'vitest';

import * as start from '../../../../src/workshop/m5/s08_composition/start/SeatSelection.js';
import * as step1 from '../../../../src/workshop/m5/s08_composition/step1/SeatSelection.js';
import * as step2 from '../../../../src/workshop/m5/s08_composition/step2/SeatSelection.js';

const ROW_H = ['H7', 'H8'];

/** Pułapka self-use przy dziedziczeniu i pułapki delegowania - dokumentacja zachowania każdego wariantu. */
describe('S08SolutionTest', () => {
  it('startCountsBulkSelectionTwiceBecauseAddAllCallsAdd', () => {
    const selection = new start.SeatSelection();
    selection.addAll(ROW_H);
    expect(selection.size).toBe(2);
    // pułapka: addAll() dodaje przez add() na this, a add() jest nadpisane i też liczy
    expect(selection.clicks()).toBe(4);
  });

  it('startExposesWholeSetApiThatBypassesTheCounter', () => {
    const selection = new start.SeatSelection();
    selection.add('H7');
    expect(selection).toBeInstanceOf(Set);
    // przez unknown - jak Object w Javie: plik typuje się także po jump/next, gdy start nie jest już Set
    const inheritedApi = selection as unknown as Set<string>;
    inheritedApi.clear();
    expect(inheritedApi.size).toBe(0);
    // clear() z odziedziczonego API omija licznik
    expect(selection.clicks()).toBe(1);
  });

  it('delegationCountsBulkSelectionOnce', () => {
    const first = new step1.SeatSelection();
    first.addAll(ROW_H);
    expect(first.clicks()).toBe(2);
    const second = new step2.SeatSelection();
    second.addAll(ROW_H);
    expect(second.clicks()).toBe(2);
  });

  it('generatedGetterLeaksTheDelegate', () => {
    const selection = new step1.SeatSelection();
    selection.getSeats().add('Z1');
    expect(selection.size()).toBe(1);
    // pułapka: zmiana przez delegata omija licznik
    expect(selection.clicks()).toBe(0);
  });

  it('solutionIsNarrowFacadeWithDefensiveCopy', () => {
    const selection = new step2.SeatSelection();
    selection.addAll(['H8', 'H7']);
    expect(selection.seats()).toEqual(['H8', 'H7']);
    // zamrożona kopia: modyfikacja rzuca TypeError (odpowiednik UnsupportedOperationException)
    expect(() => (selection.seats() as string[]).push('Z1')).toThrow(TypeError);
    // świadomie: to już nie jest Set
    expect(new step2.SeatSelection()).not.toBeInstanceOf(Set);
  });
});
