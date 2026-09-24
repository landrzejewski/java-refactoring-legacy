import { describe, expect, it } from 'vitest';

import { Ticket as StartTicket } from '../../../../src/workshop/m5/s11_constructorcall/start/Ticket.js';
import * as start from '../../../../src/workshop/m5/s11_constructorcall/start/VipTicket.js';
import * as step1 from '../../../../src/workshop/m5/s11_constructorcall/step1/VipTicket.js';
import * as step2 from '../../../../src/workshop/m5/s11_constructorcall/step2/VipTicket.js';

/** Konstruktor wołający override: pułapka w start, naprawa lokalna (step1) i strukturalna (step2). */
describe('S11SolutionTest', () => {
  it('startSubclassSeesUninitializedField', () => {
    expect(new start.VipTicket('K12', 'Salonik A').label()).toBe('Miejsce K12 (VIP: undefined)');
  });

  it('constructorPrologInitializesFieldBeforeSuper', () => {
    // W TS brak prologu konstruktora - krok 1 to lokalny override label() w podklasie.
    expect(new step1.VipTicket('K12', 'Salonik A').label()).toBe('Miejsce K12 (VIP: Salonik A)');
  });

  it('solutionComputesLabelWhenObjectIsComplete', () => {
    expect(new step2.VipTicket('K12', 'Salonik A').label()).toBe('Miejsce K12 (VIP: Salonik A)');
  });

  // Dodatkowy test TS: to samo z polem prywatnym ES. Slot #lounge powstaje dopiero po super(...),
  // więc odczyt z override wywołanego w konstruktorze bazy rzuca TypeError zamiast dać undefined.
  it('startWithPrivateNameFieldFailsFast', () => {
    class PrivateNameVipTicket extends StartTicket {
      readonly #lounge: string;

      constructor(seat: string, lounge: string) {
        super(seat);
        this.#lounge = lounge;
      }

      protected override describe(): string {
        return super.describe() + ' (VIP: ' + this.#lounge + ')';
      }
    }
    expect(() => new PrivateNameVipTicket('K12', 'Salonik A')).toThrow(TypeError);
  });
});
