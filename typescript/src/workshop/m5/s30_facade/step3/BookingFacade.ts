import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { TicketOrder } from '../TicketOrder.js';

// Krok 3: TypeScript nie ma widoczności pakietowej z Javy, więc podsystemy przeniesione
// z osobnych plików tutaj jako klasy prywatne modułu (brak export) - poza fasadą nie da się
// ich utworzyć ani zaimportować. Konstruktor fasady jest prywatny, a złożeniem podsystemów
// zajmuje się BookingFacade.create(), wołane przez korzeń kompozycji (Cinema).

/**
 * Krok 3: podsystem ukryty (prywatny dla modułu) - bramka płatności. Karty kończące się na 0000
 * są odrzucane, karta TIMEOUT symuluje awarię bramki (wyjątek, bez wpisu w dzienniku).
 */
class PaymentClient {
  private readonly entries: string[] = [];

  charge(card: string, amount: Money): boolean {
    if (card === 'TIMEOUT') {
      throw new IllegalStateError('gateway timeout');
    }
    const accepted = !card.endsWith('0000');
    this.entries.push(`${accepted ? 'CHARGED ' : 'DECLINED '}${card} ${amount.toString()}`);
    return accepted;
  }

  log(): readonly string[] {
    return [...this.entries];
  }
}

/** Krok 3: podsystem ukryty (prywatny dla modułu) - cennik biletu 2D wg typu (N, S, E, C). */
class PriceList {
  private static readonly PRICES: ReadonlyMap<string, Money> = new Map([
    ['N', Money.of('25.00')], ['S', Money.of('18.75')], ['E', Money.of('17.50')], ['C', Money.of('15.00')],
  ]);

  price(ticketType: string): Money {
    const price = PriceList.PRICES.get(ticketType);
    if (price === undefined) {
      throw new IllegalArgumentError(`unknown ticket type: ${ticketType}`);
    }
    return price;
  }
}

/**
 * Krok 3: podsystem ukryty (prywatny dla modułu) - zajętość miejsc, rezerwacja atomowa.
 * Dostęp tylko przez BookingFacade; w prawdziwym kodzie kanały sprzedaży leżałyby w innym module.
 */
class SeatRegistry {
  private readonly taken = new Map<string, Set<string>>();

  reserve(screening: string, seats: readonly string[]): void {
    let current = this.taken.get(screening);
    if (current === undefined) {
      current = new Set<string>();
      this.taken.set(screening, current);
    }
    for (const seat of seats) {
      if (current.has(seat)) {
        throw new IllegalStateError(`seat taken ${seat}`);
      }
    }
    for (const seat of seats) {
      current.add(seat);
    }
  }

  release(screening: string, seats: readonly string[]): void {
    const current = this.taken.get(screening);
    if (current !== undefined) {
      seats.forEach((seat) => current.delete(seat));
    }
  }

  // Jak TreeMap<String, TreeSet<String>>.toString() w Javie: klucze i miejsca posortowane.
  describe(): string {
    const screenings = [...this.taken.keys()].sort()
      .map((screening) => `${screening}=[${[...(this.taken.get(screening) ?? [])].sort().join(', ')}]`);
    return `{${screenings.join(', ')}}`;
  }
}

/** Krok 3: podsystem ukryty (prywatny dla modułu) - wysyłka biletów mailem. */
class TicketMailer {
  private readonly outbox: string[] = [];

  send(email: string, text: string): void {
    this.outbox.push(`${email}: ${text}`);
  }

  sent(): readonly string[] {
    return [...this.outbox];
  }
}

// Jak List.toString() w Javie: [a, b].
const list = (items: readonly string[]): string => `[${items.join(', ')}]`;

/**
 * Krok 3: fasada jako jedyne wejście do podsystemu i strażnik spójności. Podsystemy są
 * prywatne dla modułu, a konstruktor fasady też - kanały sprzedaży dostają gotowy obiekt.
 * Niezmiennik "miejsce zajęte tylko po udanej płatności" jest egzekwowany zawsze,
 * także gdy bramka rzuci wyjątkiem (finally). Kod klienta nie może już pominąć kroku,
 * bo nie ma do kroków dostępu.
 */
export class BookingFacade {
  private constructor(
    private readonly seats: SeatRegistry,
    private readonly prices: PriceList,
    private readonly payments: PaymentClient,
    private readonly mailer: TicketMailer,
  ) {}

  /** Składa podsystemy w fasadę (w Javie robi to Cinema przez konstruktor pakietowy). */
  static create(): BookingFacade {
    return new BookingFacade(new SeatRegistry(), new PriceList(), new PaymentClient(), new TicketMailer());
  }

  book(order: TicketOrder, feePerSeat: Money): string {
    const total = this.prices.price(order.ticketType).plus(feePerSeat).times(order.seats.length);
    try {
      this.seats.reserve(order.screening, order.seats);
    } catch (exception) {
      if (exception instanceof IllegalStateError) {
        return `ERROR ${exception.message}`;
      }
      throw exception;
    }
    let paid = false;
    try {
      paid = this.payments.charge(order.card, total);
    } finally {
      if (!paid) {
        this.seats.release(order.screening, order.seats);
      }
    }
    if (!paid) {
      return 'ERROR payment declined';
    }
    this.mailer.send(order.email, `Bilety ${order.screening} ${order.seats.join(',')} = ${total.toString()}`);
    return `OK ${total.toString()}`;
  }

  /** Obserwowalny stan wszystkich podsystemów naraz (w Javie czyta go Cinema wprost z podsystemów). */
  state(): string {
    return `miejsca=${this.seats.describe()} platnosci=${list(this.payments.log())} maile=${list(this.mailer.sent())}`;
  }
}
