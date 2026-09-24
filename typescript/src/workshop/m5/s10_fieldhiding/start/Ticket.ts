/**
 * Start: pole `#type` i metoda statyczna `category()` wyglądają na "nadpisywalne", ale nie są.
 * Pole prywatne ES (#) należy do klasy, która je deklaruje: podklasa z własnym `#type` ma w tym samym
 * obiekcie DRUGI, niezależny slot. label() jest zapisane w Ticket, więc czyta slot Ticket i woła
 * Ticket.category() - także dla obiektu StudentTicket.
 */
export class Ticket {
  #type = 'NORMAL';

  static category(): string {
    return 'BILET';
  }

  type(): string {
    return this.#type;
  }

  label(): string {
    return Ticket.category() + ': ' + this.#type;
  }
}
