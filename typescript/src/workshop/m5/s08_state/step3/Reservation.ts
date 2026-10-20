import { IllegalStateError } from '../../../../shared/errors.js';
import type { Payments } from '../Payments.js';
import type { ReservationActions } from '../ReservationActions.js';
import { Status } from '../Status.js';

type Transition = (reservation: Reservation) => void;

interface ReservationState {
  readonly status: Status;
  readonly pay: Transition;
  readonly use: Transition;
  readonly expire: Transition;
  readonly cancel: Transition;
}

/** Domyślne przejście rzuca wyjątek (nie jest puste!) - stan podaje tylko dozwolone przejścia. */
function state(status: Status, allowed: Partial<Omit<ReservationState, 'status'>> = {}): ReservationState {
  const invalid = (action: string): Transition => () => {
    throw new IllegalStateError(`cannot ${action} in ${status}`);
  };
  return {
    status,
    pay: invalid('pay'),
    use: invalid('use'),
    expire: invalid('expire'),
    cancel: invalid('cancel'),
    ...allowed,
  };
}

/**
 * Krok 3: expire i cancel w stanach - kontekst tylko deleguje. Tabela przejść jest czytelna
 * wprost z kodu stanów; stany są bezstanowymi stałymi, dane zostają w Reservation.
 */
export class Reservation implements ReservationActions {
  private static readonly NEW: ReservationState = state(Status.NEW, {
    pay: (reservation) => {
      reservation.charge();
      reservation.moveTo(Reservation.PAID);
      reservation.record('charged');
    },
    expire: (reservation) => {
      reservation.moveTo(Reservation.EXPIRED);
      reservation.record('seats released');
    },
    cancel: (reservation) => {
      reservation.moveTo(Reservation.CANCELLED);
      reservation.record('seats released');
    },
  });

  private static readonly PAID: ReservationState = state(Status.PAID, {
    use: (reservation) => {
      reservation.moveTo(Reservation.USED);
      reservation.record('gate opened');
    },
    cancel: (reservation) => {
      reservation.moveTo(Reservation.CANCELLED);
      reservation.record('refund');
      reservation.record('seats released');
    },
  });

  // Stany końcowe - żadne przejście nie jest dozwolone.
  private static readonly USED = state(Status.USED);
  private static readonly EXPIRED = state(Status.EXPIRED);
  private static readonly CANCELLED = state(Status.CANCELLED);

  private readonly effectList: string[] = [];
  private state = Reservation.NEW;

  constructor(private readonly id: string, private readonly payments: Payments) {}

  pay(): void {
    this.state.pay(this);
  }

  use(): void {
    this.state.use(this);
  }

  expire(): void {
    this.state.expire(this);
  }

  cancel(): void {
    this.state.cancel(this);
  }

  status(): Status {
    return this.state.status;
  }

  effects(): readonly string[] {
    return Object.freeze([...this.effectList]);
  }

  private charge(): void {
    this.payments.charge(this.id);
  }

  private moveTo(next: ReservationState): void {
    this.state = next;
  }

  private record(effect: string): void {
    this.effectList.push(effect);
  }
}
