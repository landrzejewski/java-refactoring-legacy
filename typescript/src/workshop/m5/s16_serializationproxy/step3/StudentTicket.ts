import { InvalidClassError } from '../SessionStore.js';
import { Ticket } from './Ticket.js';

/** Płaska postać serializowana - jedyny format danych biletu (odpowiednik record SerializedForm). */
interface SerializedForm {
  readonly v: number;
  readonly title: string;
  readonly seat: string;
  readonly studentId: string;
}

/**
 * Krok 3: bez zmian - proxy serializacji z kroku 2.
 */
export class StudentTicket extends Ticket {
  static readonly #SERIAL_VERSION = 2;

  readonly #studentId: string;

  constructor(title: string, seat: string, studentId: string) {
    super(title, seat);
    this.#studentId = studentId;
  }

  /** Odpowiednik readResolve: odczyt tylko przez postać serializowaną i publiczny konstruktor. */
  static fromJSON(data: unknown): StudentTicket {
    const form = data as Partial<SerializedForm>;
    if (form.v !== StudentTicket.#SERIAL_VERSION) {
      throw new InvalidClassError(`StudentTicket: nieobsługiwana wersja formatu ${String(form.v)}`);
    }
    return new StudentTicket(String(form.title), String(form.seat), String(form.studentId));
  }

  describe(): string {
    return this.title() + ' ' + this.seat() + ' (legitymacja ' + this.#studentId + ')';
  }

  /** Odpowiednik writeReplace: do JSON trafia płaska postać, a nie pola klas. */
  toJSON(): SerializedForm {
    return { v: StudentTicket.#SERIAL_VERSION, title: this.title(), seat: this.seat(), studentId: this.#studentId };
  }
}
