import { describe } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import type { Money } from '../../../../src/workshop/shared/Money.js';
import { DAYS_OF_WEEK, type DayOfWeek } from '../../../../src/workshop/shared/time.js';
import * as start from '../../../../src/workshop/m6/s20_decisionmap/start/ShowPricing.js';
import * as step1 from '../../../../src/workshop/m6/s20_decisionmap/step1/ShowPricing.js';
import * as step2 from '../../../../src/workshop/m6/s20_decisionmap/step2/ShowPricing.js';
import * as step3 from '../../../../src/workshop/m6/s20_decisionmap/step3/ShowPricing.js';
import { Scene } from '../../support/scene.js';

interface Pricing {
  price(day: DayOfWeek, format: string): Money;
}

// Odpowiednik DayOfWeek.valueOf(...) z Javy.
function dayOf(name: string): DayOfWeek {
  const day = DAYS_OF_WEEK.find((candidate) => candidate === name);
  if (day === undefined) {
    throw new IllegalArgumentError(`No enum constant DayOfWeek.${name}`);
  }
  return day;
}

function safe(pricing: Pricing): (input: string) => string {
  return (input) => {
    const [day = '', format = ''] = input.split(' ');
    try {
      return pricing.price(dayOf(day), format).toString();
    } catch (exception) {
      if (exception instanceof IllegalArgumentError) {
        return `ERROR ${exception.message}`;
      }
      throw exception;
    }
  };
}

/** Pełna tabela 3 formaty x typy dni: obie ścieżki (krok 2 i krok 3) dają te same ceny. */
describe('S20EquivalenceTest', () => {
  describe('bothPathsPriceTheSame', () => {
    Scene.variants<string, string>()
      .variant('start', safe(new start.ShowPricing()))
      .variant('step1', safe(new step1.ShowPricing()))
      .variant('step2 (Strategy)', safe(new step2.ShowPricing()))
      .variant('step3 (typ formatu)', safe(new step3.ShowPricing()))
      .expect('2D poniedziałek', 'MONDAY 2D', '25.00')
      .expect('2D wtorek', 'TUESDAY 2D', '17.50')
      .expect('2D sobota', 'SATURDAY 2D', '27.00')
      .expect('3D poniedziałek', 'MONDAY 3D', '32.00')
      .expect('3D wtorek', 'TUESDAY 3D', '22.40')
      .expect('3D sobota', 'SATURDAY 3D', '34.00')
      .expect('IMAX poniedziałek', 'MONDAY IMAX', '40.00')
      .expect('IMAX wtorek', 'TUESDAY IMAX', '28.00')
      .expect('IMAX niedziela', 'SUNDAY IMAX', '42.00')
      .expect('nieznany format', 'TUESDAY 4DX', 'ERROR unknown format: 4DX')
      .tests();
  });
});
