import type { Money } from '../../../shared/Money.js';

/**
 * Krok 1: Safe Composite - Push Members Down: add() i children() tylko w Combo.
 * Wspólny typ ma wyłącznie operacje sensowne dla liścia i węzła.
 */
export abstract class MenuComponent {
  abstract name(): string;

  abstract price(): Money;

  describe(): string {
    return `${this.name()} ${this.price().toString()}`;
  }
}
