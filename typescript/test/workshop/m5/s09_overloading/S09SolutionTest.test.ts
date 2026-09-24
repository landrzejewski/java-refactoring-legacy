import { describe, expect, it } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import * as start from '../../../../src/workshop/m5/s09_overloading/start/Checkout.js';
import { PriceList as StartPriceList } from '../../../../src/workshop/m5/s09_overloading/start/PriceList.js';
import { StudentTicket as StartStudentTicket } from '../../../../src/workshop/m5/s09_overloading/start/StudentTicket.js';
import { Ticket as StartTicket } from '../../../../src/workshop/m5/s09_overloading/start/Ticket.js';
import * as step1 from '../../../../src/workshop/m5/s09_overloading/step1/Checkout.js';
import { StudentTicket as Step1StudentTicket } from '../../../../src/workshop/m5/s09_overloading/step1/StudentTicket.js';
import { Ticket as Step1Ticket } from '../../../../src/workshop/m5/s09_overloading/step1/Ticket.js';
import * as step2 from '../../../../src/workshop/m5/s09_overloading/step2/Checkout.js';
import { StudentTicket as Step2StudentTicket } from '../../../../src/workshop/m5/s09_overloading/step2/StudentTicket.js';
import { Ticket as Step2Ticket } from '../../../../src/workshop/m5/s09_overloading/step2/Ticket.js';
import { workshopDir } from '../../support/paths.js';
import { compileErrors } from '../reflection.js';

const IMAX = Money.of('40.00');
const STANDARD_2D = Money.of('25.00');

/** Wybór wariantu po typie deklarowanym kontra dyspozycja dynamiczna - wynik każdego wariantu. */
describe('S09SolutionTest', () => {
  it('startPicksOverloadByDeclaredType', () => {
    const declaredAsBase: StartTicket = new StartStudentTicket('Amator', STANDARD_2D);
    // pułapka: price(Ticket)
    expect(new StartPriceList().price(declaredAsBase)).toEqual(Money.of('25.00'));
    const cart: StartTicket[] = [new StartTicket('Diuna', IMAX), new StartStudentTicket('Amator', STANDARD_2D)];
    // student zapłacił pełną cenę
    expect(new start.Checkout().total(cart)).toEqual(Money.of('65.00'));
  });

  it('overridingDispatchesOnRuntimeClass', () => {
    const cart: Step1Ticket[] = [new Step1Ticket('Diuna', IMAX), new Step1StudentTicket('Amator', STANDARD_2D)];
    expect(new step1.Checkout().total(cart)).toEqual(Money.of('58.75'));
  });

  it('overloadedEqualsIsInvisibleToCollections', () => {
    const cart = [new StartTicket('Diuna', IMAX)];
    const same = new StartTicket('Diuna', IMAX);
    // bezpośrednie wywołanie equals działa
    expect(cart[0]!.equals(same)).toBe(true);
    // pułapka: includes() porównuje tożsamość, equals nie widzi
    expect(new start.Checkout().alreadyInCart(cart, same)).toBe(false);
    expect(new step1.Checkout().alreadyInCart([new Step1Ticket('Diuna', IMAX)], new Step1Ticket('Diuna', IMAX))).toBe(false);
  });

  it('solutionOverridesEqualsAndHashCode', () => {
    const cart = [new Step2Ticket('Diuna', IMAX)];
    expect(new step2.Checkout().alreadyInCart(cart, new Step2Ticket('Diuna', IMAX))).toBe(true);
    expect(new step2.Checkout().alreadyInCart(cart, new Step2StudentTicket('Diuna', IMAX))).toBe(false);
    expect(new Step2Ticket('Diuna', IMAX).key()).toBe(new Step2Ticket('Diuna', IMAX).key());
  });

  // Dodatkowy test TS: odpowiednik @Override. Zawężony parametr equals(other: Ticket) przechodzi
  // przez kontrakt zapisany jako metoda (biwariancja), ale nie przez kontrakt Equatable sceny.
  it('narrowedEqualsParameterIsCaughtOnlyByFunctionTypedContract', () => {
    const probe = workshopDir('m5', 's09_overloading', 'start', '__probe__.ts');
    const narrowTicket = `
      import { Money } from '../../../shared/Money.js';
      class Ticket CONTRACT {
        constructor(readonly title: string, readonly basePrice: Money) {}
        equals(other: Ticket): boolean {
          return this.title === other.title && this.basePrice.equals(other.basePrice);
        }
      }
      export const ticket = new Ticket('Diuna', Money.of('40.00'));
    `;
    const methodContract = 'interface MethodEquatable { equals(other: unknown): boolean }\n'
      + narrowTicket.replace('CONTRACT', 'implements MethodEquatable');
    const sceneContract = "import type { Equatable } from '../Equatable.js';\n"
      + narrowTicket.replace('CONTRACT', 'implements Equatable');
    expect(compileErrors({ [probe]: methodContract })).toEqual([]);
    expect(compileErrors({ [probe]: sceneContract })).toEqual(['TS2416']);
  });
});
