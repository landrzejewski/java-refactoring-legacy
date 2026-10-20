import { IllegalStateError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import { Status } from '../Status.js';

type Transition = (booking: Booking) => Money;

/** Prywatny typ stanu (w Javie: prywatny enum State w Booking) - nie jest eksportowany z modułu. */
interface BookingState {
  readonly status: Status;
  readonly pay: Transition;
  readonly use: (booking: Booking) => void;
  readonly cancel: Transition;
}

/** Domyślnie przejście rzuca wyjątek - stan podaje tylko swoje dozwolone przejścia. */
function state(status: Status, allowed: Partial<Omit<BookingState, 'status'>> = {}): BookingState {
  const invalid = (action: string) => (): never => {
    throw new IllegalStateError(`cannot ${action} in ${status}`);
  };
  return { status, pay: invalid('pay'), use: invalid('use'), cancel: invalid('cancel'), ...allowed };
}

/**
 * Krok 3: Replace State-Altering Conditionals with State - cykl życia rezerwacji.
 * Każdy stan zna tylko swoje dozwolone przejścia; domyślnie przejście rzuca wyjątek.
 * CANCEL w NEW zwalnia rezerwację, w PAID oddaje pieniądze - ta sama operacja, inne zachowanie.
 * Stan nie wie, kto o operację poprosił (Command) ani jak liczono cenę (Strategy).
 */
export class Booking {
  private static readonly NEW: BookingState = state(Status.NEW, {
    pay: (booking) => {
      booking.state = Booking.PAID;
      return booking.amount;
    },
    cancel: (booking) => {
      booking.state = Booking.CANCELLED;
      return Money.ZERO;
    },
  });

  private static readonly PAID: BookingState = state(Status.PAID, {
    use: (booking) => {
      booking.state = Booking.USED;
    },
    cancel: (booking) => {
      booking.state = Booking.CANCELLED;
      return booking.amount;
    },
  });

  private static readonly USED = state(Status.USED);
  private static readonly CANCELLED = state(Status.CANCELLED);

  private static readonly BY_STATUS: Readonly<Record<Status, BookingState>> = {
    [Status.NEW]: Booking.NEW,
    [Status.PAID]: Booking.PAID,
    [Status.USED]: Booking.USED,
    [Status.CANCELLED]: Booking.CANCELLED,
  };

  private readonly amount: Money;
  private state = Booking.NEW;

  constructor(amount: Money) {
    this.amount = requireNonNull(amount, 'amount');
  }

  status(): Status {
    return this.state.status;
  }

  /** Zwraca kwotę pobraną do kasy. */
  pay(): Money {
    return this.state.pay(this);
  }

  use(): void {
    this.state.use(this);
  }

  /** Zwraca kwotę oddaną klientowi (zero, gdy rezerwacja nie była opłacona). */
  cancel(): Money {
    return this.state.cancel(this);
  }

  /**
   * Dla cofania poleceń: powrót do zapamiętanego stanu z pominięciem reguł przejść.
   * (W Javie pakietowa; w TypeScripcie publiczna, bo woła ją BookingLedger z innego modułu.)
   */
  restore(status: Status): void {
    this.state = Booking.BY_STATUS[status];
  }
}
