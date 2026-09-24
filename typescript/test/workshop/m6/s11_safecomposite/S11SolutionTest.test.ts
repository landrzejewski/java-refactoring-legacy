import { describe, expect, it } from 'vitest';

import { UnsupportedOperationError } from '../../../../src/shared/errors.js';
import * as startProduct from '../../../../src/workshop/m6/s11_safecomposite/start/Product.js';
import * as step1Component from '../../../../src/workshop/m6/s11_safecomposite/step1/MenuComponent.js';
import * as step1Product from '../../../../src/workshop/m6/s11_safecomposite/step1/Product.js';
import type { MenuComponent } from '../../../../src/workshop/m6/s11_safecomposite/step2/MenuComponent.js';
import { Combo } from '../../../../src/workshop/m6/s11_safecomposite/step2/Combo.js';

type Add = (this: unknown, child: unknown) => void;

// Odpowiednik refleksji z Javy: szukamy metody po nazwie, żeby test kompilował się także
// po "warsztat.sh jump" (gdy start nie ma już add()).
function findMethod(type: { prototype: object }, name: string): Add | undefined {
  const method: unknown = Reflect.get(type.prototype, name);
  return typeof method === 'function' ? (method as Add) : undefined;
}

/** Transparent kontra Safe: gdzie wychodzi błąd add() na liściu. */
describe('S11SolutionTest', () => {
  /** Dokumentuje pułapkę start; po "warsztat.sh jump" (start = krok 1+) test jest pomijany. */
  it('transparentCompositeFailsAtRuntime', (context) => {
    const add = findMethod(startProduct.Product, 'add');
    if (add === undefined) {
      context.skip('start nie jest już Transparent Composite');
      return;
    }
    const nachos = new startProduct.Product('Nachos', '14.00');
    const sauce = new startProduct.Product('Sos', '3.00');
    expect(() => add.call(nachos, sauce)).toThrow(UnsupportedOperationError);
    expect(() => add.call(nachos, sauce)).toThrow('cannot add to Nachos');
  });

  it('safeCompositeHasNoAddOnLeafOrCommonType', () => {
    expect(findMethod(step1Product.Product, 'add')).toBeUndefined();
    expect(findMethod(step1Component.MenuComponent, 'children')).toBeUndefined();
  });

  it('immutableCompositeHasNoAddAtAll', () => {
    const combo = Combo.of('Zestaw');
    expect(() => (combo.children as MenuComponent[]).push(combo)).toThrow(TypeError);
  });
});
