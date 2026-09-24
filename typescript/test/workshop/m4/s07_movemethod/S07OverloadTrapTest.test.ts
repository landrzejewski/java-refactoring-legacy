import { describe, expect, it } from 'vitest';

import { Screening } from '../../../../src/workshop/m4/s07_movemethod/step2/Screening.js';
import { LocalDateTime } from '../../../../src/workshop/shared/time.js';

/** Tak wyglądałoby Screening.freeSeatsWithout po "sprzątaniu" przy przenosinach: splice(seat, 1). */
function naiveFreeSeatsWithout(freeSeats: readonly number[], seat: number): number[] {
  const free = [...freeSeats];
  free.splice(seat, 1);
  return free;
}

/**
 * Dokumentuje pułapkę: przy przenoszeniu ktoś "sprząta" `splice(indexOf(seat), 1)` do `splice(seat, 1)`,
 * bo w nowym właścicielu seat to po prostu number. Kod się kompiluje, ale usuwa po pozycji, nie po wartości.
 */
describe('S07OverloadTrapTest', () => {
  it('integerParameterRemovesTheSeatNumber', () => {
    expect(new Screening('Diuna', 3, LocalDateTime.of(2026, 9, 25, 20, 0), 1, [1, 2, 3, 4, 5])
      .freeSeatsWithout(1)).toEqual([2, 3, 4, 5]);
  });

  it('intParameterRemovesTheElementAtIndex', () => {
    // splice(1, 1) usunął miejsce nr 2, a zostawił zarezerwowane nr 1.
    expect(naiveFreeSeatsWithout([1, 2, 3, 4, 5], 1)).toEqual([1, 3, 4, 5]);
    // Java rzuca IndexOutOfBoundsException; splice poza tablicą po cichu nic nie usuwa.
    expect(naiveFreeSeatsWithout([7, 8, 9], 8)).toEqual([7, 8, 9]);
  });
});
