import { describe, expect, it } from 'vitest';

import { Format } from '../../../../src/workshop/m6/s03_typecode/step3/Format.js';
import { FormatCodes } from '../../../../src/workshop/m6/s03_typecode/step3/FormatCodes.js';

/** Granica trwałości: w bazie zostaje stabilny kod, nie pozycja (ordinal) ani nazwa stałej. */
describe('S03SolutionTest', () => {
  it('persistentCodeIsNotTheOrdinal', () => {
    const ordinal = Format.values().indexOf(Format.IMAX);
    expect(FormatCodes.toCode(Format.IMAX)).not.toBe(ordinal);
    expect(FormatCodes.toCode(Format.IMAX)).toBe(3);
  });

  it('everyFormatSurvivesRoundTripThroughTheMapper', () => {
    for (const format of Format.values()) {
      expect(FormatCodes.fromCode(FormatCodes.toCode(format))).toBe(format);
    }
  });
});
