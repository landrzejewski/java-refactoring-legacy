import type { Money } from '../../../shared/Money.js';
import type { Ticket } from '../Ticket.js';
import type { PriceRule } from './PriceRule.js';
import { StandardRule } from './StandardRule.js';
import { StudentRule } from './StudentRule.js';

/**
 * Krok 1: jedyna zmiana to typ reguł - PriceRule<Ticket> zamiast object, bez rzutowania przy apply.
 * Kompiluje się, bo PriceRule<StudentTicket> przypisuje się do PriceRule<Ticket> (biwariancja metod).
 * Zgadywanie typu biletu z nazwy klasy zostaje - wymazane T nic rejestrowi nie mówi.
 */
export class RuleRegistry {
  readonly #rules = new Map<string, PriceRule<Ticket>>();

  constructor(...rules: PriceRule<Ticket>[]) {
    for (const rule of rules) {
      this.#rules.set(rule.constructor.name.replace(/Rule$/, 'Ticket'), rule);
    }
  }

  price(ticket: Ticket): Money {
    const rule = this.#rules.get(ticket.constructor.name) as PriceRule<Ticket>;
    return rule.apply(ticket);
  }

  supportedTypes(): string[] {
    return [...this.#rules.keys()].sort();
  }

  static standard(): RuleRegistry {
    return new RuleRegistry(new StandardRule(), new StudentRule());
  }
}
