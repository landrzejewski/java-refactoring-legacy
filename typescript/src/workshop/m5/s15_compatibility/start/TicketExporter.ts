import { IllegalStateError } from '../../../../shared/errors.js';
import { columnOf } from '../Column.js';
import type { Ticket } from './Ticket.js';

/**
 * Start: legacy eksporter szuka kolumn tylko w klasie runtime (własne nazwy prototypu obiektu,
 * odpowiednik getDeclaredMethods()). Działa wyłącznie dlatego, że każda podklasa sama deklaruje price().
 * Po Pull Up przestanie.
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

  // Tylko metody zadeklarowane w klasie runtime (odpowiednik getDeclaredMethods()).
  static #candidates(ticket: Ticket): Array<[string, unknown]> {
    const prototype: object = Object.getPrototypeOf(ticket);
    return Object.getOwnPropertyNames(prototype).map((name) => [name, Reflect.get(prototype, name)]);
  }

  static #read(method: unknown, ticket: Ticket): unknown {
    if (typeof method !== 'function') {
      throw new IllegalStateError('kolumna nie jest metodą');
    }
    return method.call(ticket);
  }
}
