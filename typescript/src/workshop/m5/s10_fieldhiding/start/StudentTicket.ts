import { Ticket } from './Ticket.js';

/**
 * Start: podklasa UKRYWA pole i metodę statyczną bazy. W obiekcie są teraz dwa niezależne sloty
 * "#type"; który zobaczysz, zależy od klasy, w której stoi kod, a nie od klasy obiektu.
 */
export class StudentTicket extends Ticket {
  #type = 'STUDENT';

  static override category(): string {
    return 'BILET ULGOWY';
  }

  override type(): string {
    return this.#type;
  }
}
