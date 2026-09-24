import { expect, it } from 'vitest';

/**
 * Pomocnik testów równoważności scen warsztatu.
 * Każdy wariant sceny (start, step1, ...) jest adaptowany do wspólnej
 * funkcji I -> O, a następnie sprawdzany na tych samych przypadkach.
 *
 *   describe('S01EquivalenceTest', () => {
 *     Scene.variants<Order, string>()
 *       .variant('start', (o) => new start.PriceCalculator().price(o))
 *       .variant('step1', (o) => new step1.PriceCalculator().price(o))
 *       .expect('2D normal', order2d, '25.00')
 *       .tests();
 *   });
 */
export class Scene<I, O> {
  private readonly variantList: Array<[string, (input: I) => O]> = [];
  private readonly cases: Array<{ name: string; input: I; expected: O }> = [];

  private constructor() {}

  static variants<I, O>(): Scene<I, O> {
    return new Scene<I, O>();
  }

  variant(name: string, implementation: (input: I) => O): this {
    this.variantList.push([name, implementation]);
    return this;
  }

  expect(name: string, input: I, expected: O): this {
    this.cases.push({ name, input, expected });
    return this;
  }

  // Rejestruje w bieżącym describe jeden test na każdą parę "wariant: przypadek".
  tests(): void {
    for (const [variant, implementation] of this.variantList) {
      for (const testCase of this.cases) {
        it(`${variant}: ${testCase.name}`, () => {
          expect(implementation(testCase.input)).toEqual(testCase.expected);
        });
      }
    }
  }
}
