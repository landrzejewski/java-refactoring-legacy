import ts from 'typescript-api';
import { describe, expect, it } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { Cart as Step1Cart } from '../../../../src/workshop/m5/s06_extractinterface/step1/Cart.js';
import { Cart } from '../../../../src/workshop/m5/s06_extractinterface/step3/Cart.js';
import * as priceable from '../../../../src/workshop/m5/s06_extractinterface/step3/Priceable.js';
import { Ticket } from '../../../../src/workshop/m5/s06_extractinterface/step3/Ticket.js';
import { sourceOf } from '../reflection.js';

/** Nowa implementacja spoza pierwotnej hierarchii: okulary 3D (+3.00, VAT 23%). */
class Glasses3D implements priceable.Priceable {
  price(): Money {
    return Money.of('3.00');
  }

  vatPercent(): number {
    return 23;
  }
}

/** Rola klienta zamiast wielu metod per typ; rozszerzenie roli bez łamania implementacji. */
describe('S06SolutionTest', () => {
  it('beforeMigrationCartHasOneAddPerConcreteType', () => {
    // W Javie dwa przeciążenia add(...); w TS - dwie osobne metody addTicket/addSnack.
    expect(Object.getOwnPropertyNames(Step1Cart.prototype).filter((name) => name.startsWith('add'))).toHaveLength(2);
  });

  it('solutionCartDependsOnlyOnTheRole', () => {
    expect(Object.getOwnPropertyNames(Cart.prototype).filter((name) => name.startsWith('add'))).toEqual(['add']);
    // Odpowiednik isDefault(): vatAmount nie jest wymaganą składową interfejsu (implementacje
    // nic nie muszą), tylko funkcją obok niego, zbudowaną na operacjach kontraktu.
    const source = sourceOf('s06_extractinterface', 'step3', 'Priceable.ts');
    const role = source.statements.find(
      (statement): statement is ts.InterfaceDeclaration =>
        ts.isInterfaceDeclaration(statement) && statement.name.text === 'Priceable',
    );
    expect(role?.members.map((member) => member.name?.getText(source))).toEqual(['price', 'vatPercent']);
    expect(typeof priceable.vatAmount).toBe('function');
  });

  it('newImplementationGetsDefaultMethodForFree', () => {
    expect(priceable.vatAmount(new Glasses3D())).toEqual(Money.of('0.56'));
    const cart = new Cart();
    cart.add(new Ticket('Kraina Lodu', 'B4', Money.of('32.00')));
    cart.add(new Glasses3D());
    expect(cart.summary()).toBe('Razem: 35.00, VAT: 2.93');
  });
});
