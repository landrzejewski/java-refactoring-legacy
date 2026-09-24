import { IllegalStateError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import type { Ticket } from '../Ticket.js';
import type { PriceRule, TicketType } from './PriceRule.js';
import { StandardRule } from './StandardRule.js';
import { StudentRule } from './StudentRule.js';

/**
 * Krok 2: rejestr bez zgadywania - typ z kontraktu, sprawdzenie instanceof (odpowiednik Class.cast)
 * w jednym miejscu. PriceRule<Ticket> w parametrach pełni rolę PriceRule<?> z Javy.
 */
export class RuleRegistry {
  readonly #rules = new Map<TicketType<Ticket>, PriceRule<Ticket>>();

  constructor(...rules: PriceRule<Ticket>[]) {
    for (const rule of rules) {
      this.#rules.set(rule.ticketType(), rule);
    }
  }

  price(ticket: Ticket): Money {
    const rule = this.#rules.get(ticket.constructor as TicketType<Ticket>);
    if (rule === undefined) {
      throw new IllegalStateError('brak reguły dla ' + ticket.constructor.name);
    }
    return RuleRegistry.#apply(rule, ticket);
  }

  static #apply<T extends Ticket>(rule: PriceRule<T>, ticket: Ticket): Money {
    const type = rule.ticketType();
    if (!(ticket instanceof type)) {
      throw new TypeError(`${ticket.constructor.name} nie jest ${type.name}`);
    }
    return rule.apply(ticket);
  }

  supportedTypes(): string[] {
    return [...this.#rules.keys()].map((type) => type.name).sort();
  }

  static standard(): RuleRegistry {
    return new RuleRegistry(new StandardRule(), new StudentRule());
  }
}
