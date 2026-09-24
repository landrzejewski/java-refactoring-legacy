import { describe, expect, it } from 'vitest';

import { Clump, DAY, runStart, runStep1, runStep2, runStep3 } from './S06Observations.js';

/**
 * Krok 3 przesuwa moment walidacji: wyjątek leci przy tworzeniu ScreeningSlot,
 * zanim wywołamy jakąkolwiek metodę. describe() dla złej sali przestaje działać.
 */
const HALL_12 = new Clump('S1', DAY, 12, '2D');
const FORMAT_4DX = new Clump('S1', DAY, 3, '4DX');

describe('S06ValidationMomentTest', () => {
  it('untilStep2OnlyTicketPriceRejectsWrongHall', () => {
    const expected = 'S1 2026-03-10 sala 12 (2D) | EXC nie ma sali 12 (S1)';
    expect(runStart(HALL_12)).toBe(expected);
    expect(runStep1(HALL_12)).toBe(expected);
    expect(runStep2(HALL_12)).toBe(expected);
  });

  it('step3RejectsWrongHallWhenTheSlotIsCreated', () => {
    expect(runStep3(HALL_12)).toBe('new ScreeningSlot -> EXC nie ma sali 12 (S1)');
  });

  it('unknownFormatMovesTheSameWay', () => {
    expect(runStart(FORMAT_4DX)).toBe('S1 2026-03-10 sala 3 (4DX) | EXC nieznany format 4DX (S1)');
    expect(runStep2(FORMAT_4DX)).toBe('S1 2026-03-10 sala 3 (4DX) | EXC nieznany format 4DX (S1)');
    expect(runStep3(FORMAT_4DX)).toBe('new ScreeningSlot -> EXC nieznany format 4DX (S1)');
  });
});
