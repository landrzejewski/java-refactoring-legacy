import { describe, expect, expectTypeOf, it } from 'vitest';

import * as start from '../../../../src/workshop/m4/s09_extractclass/start/Booking.js';
import * as step1Customer from '../../../../src/workshop/m4/s09_extractclass/step1/Customer.js';
import * as step3 from '../../../../src/workshop/m4/s09_extractclass/step3/Booking.js';
import * as step3Customer from '../../../../src/workshop/m4/s09_extractclass/step3/Customer.js';
import { Money } from '../../../../src/workshop/shared/Money.js';

/** Nazwy pól instancji posortowane (także prywatnych w sensie TS - w JS to zwykłe właściwości). */
function fields(instance: object): string[] {
  return Object.keys(instance).sort();
}

/** Publiczne metody typu (poziom typów: keyof widzi tylko publiczne składowe). */
type Behaviour<T> = { [K in keyof T]: T[K] extends (...args: never[]) => unknown ? K : never }[keyof T];

/** Metody zadeklarowane na prototypie klasy (poza konstruktorem). */
function prototypeMethods(type: { prototype: object }): string[] {
  return Object.getOwnPropertyNames(type.prototype).filter((name) => name !== 'constructor').sort();
}

/** Struktura po każdym kroku: gdzie są pola i gdzie zachowanie (w tym "worek" z kroku 1). */
describe('S09StructureTest', () => {
  it('startBookingHoldsCustomerAndPaymentFields', () => {
    expect(fields(new start.Booking('B', 'n', 'e', 'p', Money.ZERO))).toEqual(['amount', 'cardNumber',
      'customerEmail', 'customerName', 'customerPhone', 'id', 'paymentStatus']);
  });

  it('step1CustomerIsADataBagWithoutBehaviour', () => {
    // Tylko pola - logika kontaktu została w Booking.
    expect(prototypeMethods(step1Customer.Customer)).toEqual([]);
    expectTypeOf<Behaviour<step1Customer.Customer>>().toEqualTypeOf<never>();
  });

  it('step3BookingComposesCustomerAndPayment', () => {
    expect(fields(new step3.Booking('B', 'n', 'e', 'p', Money.ZERO))).toEqual(['amount', 'customer', 'id', 'payment']);
    expectTypeOf<Behaviour<step3Customer.Customer>>().toEqualTypeOf<'contactLine'>();
  });
});
