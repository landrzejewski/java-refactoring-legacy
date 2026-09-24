import { describe, expect, it } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import * as step2 from '../../../../src/workshop/m3/s08_ocp/step2/ScreeningOffer.js';
import * as step3 from '../../../../src/workshop/m3/s08_ocp/step3/ScreeningOffer.js';

/**
 * Nowy format 4DX to zmiana zachowania, więc bez start (edytowanego na żywo):
 * w kroku 2 nieznany, w kroku 3 obsłużony bez zmiany ScreeningOffer.
 */
describe('S08NewFormatTest', () => {
  it('step2DoesNotKnow4dxYet', () => {
    expect(() => new step2.ScreeningOffer().price('4DX', false)).toThrow(IllegalArgumentError);
  });

  it('step3Prices4dxWithGlasses', () => {
    const offer = new step3.ScreeningOffer();
    expect(offer.price('4DX', false).toFixed(2)).toBe('48.00');
    expect(offer.price('4DX', true).toFixed(2)).toBe('45.00');
    expect(offer.label('4DX')).toBe('4DX - ruchome fotele');
  });
});
