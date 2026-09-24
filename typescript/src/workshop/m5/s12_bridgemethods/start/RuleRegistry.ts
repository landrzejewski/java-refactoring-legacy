import type { Money } from '../../../shared/Money.js';
import type { Ticket } from '../Ticket.js';
import { StandardRule } from './StandardRule.js';
import { StudentRule } from './StudentRule.js';

/**
 * Start: rejestr "frameworkowy". Typ parametru apply(...) jest w czasie działania wymazany
 * (TS nie zostawia typów), więc rejestr zgaduje obsługiwany bilet z konwencji nazw:
 * StudentRule -> StudentTicket. Działa, dopóki nazwa klasy reguły pasuje do nazwy klasy biletu.
 */
export class RuleRegistry {
  readonly #rules = new Map<string, object>();

  constructor(...rules: object[]) {
    for (const rule of rules) {
      this.#rules.set(rule.constructor.name.replace(/Rule$/, 'Ticket'), rule);
    }
  }

  price(ticket: Ticket): Money {
    const rule = this.#rules.get(ticket.constructor.name) as { apply(ticket: Ticket): Money };
    return rule.apply(ticket);
  }

  supportedTypes(): string[] {
    return [...this.#rules.keys()].sort();
  }

  static standard(): RuleRegistry {
    return new RuleRegistry(new StandardRule(), new StudentRule());
  }
}
