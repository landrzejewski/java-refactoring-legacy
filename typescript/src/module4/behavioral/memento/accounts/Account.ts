import { Decimal } from 'decimal.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Memento } from './Memento.js';

export class Account {
  private readonly number: string; // Java: UUID
  private balance: Decimal = new Decimal(0);

  constructor(number: string) {
    this.number = requireNonNull(number);
  }

  deposit(amount: Decimal): void {
    this.balance = this.balance.add(amount);
  }

  createMemento(): Memento {
    return new Memento(this.balance);
  }

  restoreMemento(memento: Memento): void {
    this.balance = memento.balance();
  }

  getBalance(): Decimal {
    return this.balance;
  }

  toString(): string {
    return `Account(number=${this.number}, balance=${this.balance.toString()})`;
  }
}
