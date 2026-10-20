import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { Status } from '../Status.js';
import { TicketPricings } from './TicketPricings.js';

/** Wpis dziennika: z niego liczone jest cofanie - osobny switch po nazwie operacji. */
class Entry {
  constructor(
    readonly operation: string,
    readonly id: string,
    readonly previous: Status | null,
    readonly moved: Money,
  ) {}
}

/**
 * Krok 1: oś "cena wg typu biletu" wydzielona jako Strategy - TicketPricings wybiera strategię
 * po kodzie typu, a pulpit tylko ją woła. Operacje i cykl życia bez zmian (jedna oś naraz).
 */
export class BoxOfficeDesk {
  private static readonly FORMATS: ReadonlyMap<string, Money> = new Map([
    ['2D', Money.of('25.00')],
    ['3D', Money.of('32.00')],
    ['IMAX', Money.of('40.00')],
  ]);

  private readonly statuses = new Map<string, Status>();
  private readonly amounts = new Map<string, Money>();
  private readonly journal: Entry[] = [];
  private till = Money.ZERO;

  handle(line: string): string {
    const args = line.trim().split(/ +/);
    const operation = (args[0] ?? '').toUpperCase();
    try {
      switch (operation) {
        case 'BOOK':
          return this.book(args);
        case 'PAY':
        case 'USE':
        case 'CANCEL':
          return this.change(operation, args);
        case 'UNDO':
          return this.undo();
        case 'REPORT':
          return this.report();
        default:
          return `Nieznana operacja: ${args[0] ?? ''}`;
      }
    } catch (exception) {
      if (exception instanceof IllegalArgumentError || exception instanceof IllegalStateError) {
        return `Blad: ${exception.message}`;
      }
      throw exception;
    }
  }

  private book(args: readonly string[]): string {
    if (args.length < 4) {
      throw new IllegalArgumentError('BOOK <id> <format> <typ>...');
    }
    const id = args[1] ?? '';
    const format = args[2] ?? '';
    const base = BoxOfficeDesk.FORMATS.get(format);
    if (base === undefined) {
      throw new IllegalArgumentError(`unknown format: ${format}`);
    }
    let total = Money.ZERO;
    for (const type of args.slice(3)) {
      total = total.plus(TicketPricings.forType(type).price(base));
    }
    if (this.statuses.has(id)) {
      throw new IllegalArgumentError(`booking exists: ${id}`);
    }
    this.statuses.set(id, Status.NEW);
    this.amounts.set(id, total);
    this.journal.push(new Entry('BOOK', id, null, Money.ZERO));
    return `${id} NEW ${total.toString()}`;
  }

  private change(operation: string, args: readonly string[]): string {
    if (args.length < 2) {
      throw new IllegalArgumentError(`${operation} <id>`);
    }
    const id = args[1] ?? '';
    const status = this.statuses.get(id);
    if (status === undefined) {
      throw new IllegalArgumentError(`no booking ${id}`);
    }
    const amount = this.amounts.get(id) ?? Money.ZERO;
    let moved = Money.ZERO;
    let next: Status | null = null;
    switch (status) {
      case Status.NEW:
        switch (operation) {
          case 'PAY':
            this.till = this.till.plus(amount);
            moved = amount;
            next = Status.PAID;
            break;
          case 'CANCEL':
            next = Status.CANCELLED;
            break;
        }
        break;
      case Status.PAID:
        switch (operation) {
          case 'USE':
            next = Status.USED;
            break;
          case 'CANCEL':
            this.till = this.till.minus(amount);
            moved = amount;
            next = Status.CANCELLED;
            break;
        }
        break;
      case Status.USED:
      case Status.CANCELLED:
        next = null;
        break;
    }
    if (next === null) {
      throw new IllegalStateError(`cannot ${operation.toLowerCase()} in ${status}`);
    }
    this.statuses.set(id, next);
    this.journal.push(new Entry(operation, id, status, moved));
    if (operation === 'PAY') {
      return `${id} ${next} ${moved.toString()}`;
    }
    return `${id} ${next}${moved.isGreaterThan(Money.ZERO) ? ` zwrot ${moved.toString()}` : ''}`;
  }

  private undo(): string {
    const last = this.journal.pop();
    if (last === undefined) {
      throw new IllegalStateError('nothing to undo');
    }
    switch (last.operation) {
      case 'BOOK':
        this.statuses.delete(last.id);
        this.amounts.delete(last.id);
        break;
      case 'PAY':
        this.till = this.till.minus(last.moved);
        this.restore(last);
        break;
      case 'CANCEL':
        this.till = this.till.plus(last.moved);
        this.restore(last);
        break;
      default:
        this.restore(last);
    }
    return `Cofnieto: ${last.operation} ${last.id}`;
  }

  private restore(entry: Entry): void {
    if (entry.previous !== null) {
      this.statuses.set(entry.id, entry.previous);
    }
  }

  private report(): string {
    const bookings = this.statuses.size === 0 ? '-' : [...this.statuses]
      .map(([id, status]) => `${id}=${status}`)
      .join(', ');
    const log = this.journal.length === 0 ? '-' : this.journal
      .map((entry) => `${entry.operation} ${entry.id}`)
      .join(', ');
    return `Kasa: ${this.till.toString()}; ${bookings}; dziennik: ${log}`;
  }
}
