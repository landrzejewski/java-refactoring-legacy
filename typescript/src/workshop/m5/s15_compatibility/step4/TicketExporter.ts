import { IllegalStateError } from '../../../../shared/errors.js';
import { columnOf } from '../Column.js';
import type { Ticket } from './Ticket.js';

/**
 * Krok 4: bez zmian.
 */
export class TicketExporter {
  export(ticket: Ticket): string {
    let row = ticket.title();
    const candidates = TicketExporter.#candidates(ticket);
    candidates
      .flatMap(([, method]) => {
        const name = columnOf(method);
        return name === undefined ? [] : [{ name, method }];
      })
      .sort((a, b) => (a.name < b.name ? -1 : a.name > b.name ? 1 : 0))
      .forEach(({ name, method }) => {
        row += ';' + name + '=' + TicketExporter.#read(method, ticket);
      });
    return row;
  }

  // Metody klasy runtime i odziedziczone - cały łańcuch prototypów (odpowiednik getMethods()).
  // Nazwa znaleziona najniżej w hierarchii zasłania tę samą nazwę wyżej, jak nadpisanie.
  static #candidates(ticket: Ticket): Array<[string, unknown]> {
    const found = new Map<string, unknown>();
    for (let prototype: object | null = Object.getPrototypeOf(ticket);
      prototype !== null && prototype !== Object.prototype;
      prototype = Object.getPrototypeOf(prototype)) {
      for (const name of Object.getOwnPropertyNames(prototype)) {
        if (!found.has(name)) {
          found.set(name, Reflect.get(prototype, name));
        }
      }
    }
    return [...found];
  }

  static #read(method: unknown, ticket: Ticket): unknown {
    if (typeof method !== 'function') {
      throw new IllegalStateError('kolumna nie jest metodą');
    }
    return method.call(ticket);
  }
}
