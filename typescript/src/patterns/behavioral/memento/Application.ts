import { randomUUID } from 'node:crypto';
import { Decimal } from 'decimal.js';
import { JulLogger } from '../../JulLogger.js';
import { Account } from './accounts/Account.js';

const log = JulLogger.getLogger('pl.training.patterns.behavioral.memento.Application');

export function run(): void {
  const account = new Account(randomUUID());
  const caretaker = Object.freeze([account.createMemento()]);
  account.deposit(new Decimal(10)); // Java: BigDecimal.TEN
  log.info(`Deposited balance: ${account.getBalance().toString()}`, 'main');
  account.restoreMemento(caretaker[0]!);
  log.info(`Restored balance: ${account.getBalance().toString()}`, 'main');
}
