import { Duration, LocalDate, LocalDateTime, systemClock } from '../../../shared/time.js';
import { Booking } from './Booking.js';
import { BookingRepository } from './BookingRepository.js';
import { Formats } from './Formats.js';
import { LegacyDb } from './LegacyDb.js';
import { LegacyPaymentGateway } from './LegacyPaymentGateway.js';
import { NotificationService } from './NotificationService.js';
import { PricingService } from './PricingService.js';

/**
 * Krok 3: BookingRepository i typ Booking zamiast unknown[] - trzeci pionowy wycinek.
 * Magiczne indeksy (b[7], b[10]...) zamienione na nazwane pola; dostęp do rezerwacji
 * ma jednego właściciela. Magazyn nadal jest globalny (LegacyDb), więc współdzielenie
 * stanu między instancjami CinemaManager się nie zmienia - to świadomie osobna decyzja.
 * Krok 1: PricingService, krok 2: NotificationService.
 */
export class CinemaManager {
  // format: 1 = 2D, 2 = 3D, 3 = IMAX
  // status: 0 = NEW, 1 = PAID, 2 = USED, 3 = EXPIRED, 4 = CANCELLED
  // typ biletu: N = normalny, S = student, E = senior, C = dziecko

  /** Hak dla testów dodany "na chwilę" w 2019 roku. */
  static clock: () => LocalDateTime = () => systemClock.now();

  private readonly pricing = new PricingService();
  private readonly notifications = new NotificationService();
  private readonly bookings = new BookingRepository();

  addScreening(id: string, title: string, format: number,
    start: LocalDateTime, rows: number, seatsPerRow: number, vipFromRow: number): void {
    LegacyDb.SCREENINGS.set(id, [title, format, start, rows,
      seatsPerRow, vipFromRow, new Set<string>()]);
  }

  book(screeningId: string, email: string, phone: string | null,
    seats: string[] | null, types: string[] | null, web: boolean, ownGlasses: boolean): string {
    const s = LegacyDb.SCREENINGS.get(screeningId);
    if (s != null) {
      if (seats != null && seats.length > 0) {
        if (types != null && types.length === seats.length) {
          const taken = s[6] as Set<string>;
          for (const seat of seats) {
            if (taken.has(seat)) {
              return 'ERROR: seat taken ' + seat;
            }
            const row = parseInt(seat.substring(1), 10);
            const letter = seat.charCodeAt(0);
            if (row > (s[3] as number) || letter - 'A'.charCodeAt(0) >= (s[4] as number)) {
              return 'ERROR: no such seat ' + seat;
            }
          }
          const sum = this.pricing.ticketsSum(s[1] as number, s[2] as LocalDateTime, s[5] as number,
            seats, types, ownGlasses);
          const total = sum + this.pricing.bookingFee(web, seats.length);
          const id = this.bookings.nextId();
          for (const seat of seats) {
            taken.add(seat);
          }
          this.bookings.save(new Booking(id, screeningId, email, phone, seats, types, web,
            total, CinemaManager.clock(), sum));
          this.notifications.bookingCreated(email, id, s[0] as string, seats, total);
          return id;
        } else {
          return 'ERROR: types do not match seats';
        }
      } else {
        return 'ERROR: no seats';
      }
    } else {
      return 'ERROR: no screening ' + screeningId;
    }
  }

  pay(bookingId: string, card: string | null): string {
    const b = this.bookings.find(bookingId);
    if (b == null) {
      return 'ERROR: no booking';
    }
    const status = b.status;
    if (status === 1) {
      return 'ERROR: already paid';
    } else if (status === 2) {
      return 'ERROR: already used';
    } else if (status === 3) {
      return 'ERROR: expired';
    } else if (status === 4) {
      return 'ERROR: cancelled';
    }
    if (!LegacyPaymentGateway.charge(card, b.total)) {
      this.notifications.paymentDeclined(b.email, bookingId);
      return 'ERROR: payment declined';
    }
    b.markPaid(card);
    const email = b.email;
    const points = Math.trunc(b.ticketsSum / 10);
    LegacyDb.LOYALTY.set(email, (LegacyDb.LOYALTY.get(email) ?? 0) + points);
    this.notifications.ticketsPaid(email, b.phone, bookingId, b.total, points);
    return 'OK';
  }

  cancel(bookingId: string): string {
    const b = this.bookings.find(bookingId);
    if (b == null) {
      return 'ERROR: no booking';
    }
    const status = b.status;
    if (status === 2 || status === 3 || status === 4) {
      return 'ERROR: cannot cancel';
    }
    const s = LegacyDb.SCREENINGS.get(b.screeningId)!;
    const taken = s[6] as Set<string>;
    for (const seat of b.seats) {
      taken.delete(seat);
    }
    b.status = 4;
    let refund = 0;
    if (status === 1) {
      const now = CinemaManager.clock();
      const start = s[2] as LocalDateTime;
      const tickets = b.ticketsSum;
      if (!now.isBefore(start)) {
        refund = 0;
      } else if (Duration.between(now, start).toHours() >= 24) {
        refund = tickets;
      } else {
        refund = tickets * 0.5;
      }
      refund = refund - 3.00;
      if (refund < 0) {
        refund = 0;
      }
      refund = Math.round(refund * 100) / 100.0;
      if (refund > 0) {
        LegacyPaymentGateway.refund(b.card, refund);
      }
      const email = b.email;
      const points = Math.trunc(tickets / 10);
      LegacyDb.LOYALTY.set(email, Math.max(0, (LegacyDb.LOYALTY.get(email) ?? 0) - points));
    }
    this.notifications.bookingCancelled(b.email, bookingId, refund);
    return 'REFUND ' + fmt(refund);
  }

  expireOld(): void {
    const now = CinemaManager.clock();
    for (const b of this.bookings.all()) {
      if (b.status === 0
        && Duration.between(b.createdAt, now).toMinutes() >= 15) {
        b.status = 3;
        const s = LegacyDb.SCREENINGS.get(b.screeningId)!;
        const taken = s[6] as Set<string>;
        for (const seat of b.seats) {
          taken.delete(seat);
        }
        this.notifications.bookingExpired(b.email, b.id);
      }
    }
  }

  use(bookingId: string): string {
    const b = this.bookings.find(bookingId);
    if (b == null) {
      return 'ERROR: no booking';
    }
    if (b.status !== 1) {
      return 'ERROR: not paid';
    }
    b.status = 2;
    return 'OK';
  }

  loyaltyPoints(email: string): number {
    return LegacyDb.LOYALTY.get(email) ?? 0;
  }

  dailyReport(day: LocalDate): string {
    let sb = '';
    sb += 'RAPORT DZIENNY ' + day.toString() + '\n';
    const byTitle = new Map<string, number[]>();
    let fees = 0;
    let tickets = 0;
    for (const b of this.bookings.all()) {
      const status = b.status;
      if (status !== 1 && status !== 2) {
        continue;
      }
      const s = LegacyDb.SCREENINGS.get(b.screeningId)!;
      if (!(s[2] as LocalDateTime).toLocalDate().equals(day)) {
        continue;
      }
      let row = byTitle.get(s[0] as string);
      if (row === undefined) {
        row = [0, 0];
        byTitle.set(s[0] as string, row);
      }
      row[0] = row[0]! + b.seats.length;
      row[1] = row[1]! + b.ticketsSum;
      fees = fees + (b.total - b.ticketsSum);
      tickets = tickets + b.seats.length;
    }
    let revenue = 0;
    // TreeMap w oryginale - tytuły posortowane
    for (const title of [...byTitle.keys()].sort()) {
      const row = byTitle.get(title)!;
      sb += title + ': ' + Math.trunc(row[0]!) + ' bil., ' + fmt(row[1]!) + '\n';
      revenue = revenue + row[1]!;
    }
    sb += 'Biletow: ' + tickets + '\n';
    sb += 'Przychod z biletow: ' + fmt(revenue) + '\n';
    sb += 'Oplaty rezerwacyjne: ' + fmt(fees) + '\n';
    sb += 'Netto (bez VAT 8%): ' + fmt(revenue / 1.08) + '\n';
    return sb;
  }

  settlement(title: string, week: number): string {
    let revenue = 0;
    for (const b of this.bookings.all()) {
      const status = b.status;
      const s = LegacyDb.SCREENINGS.get(b.screeningId)!;
      if ((status === 1 || status === 2) && s[0] === title) {
        revenue = revenue + b.ticketsSum;
      }
    }
    let share: number;
    if (week === 1) {
      share = revenue * 0.50;
    } else if (week === 2) {
      share = revenue * 0.40;
    } else {
      share = revenue * 0.35;
    }
    if (share < 500.00) {
      share = 500.00;
    }
    return 'ROZLICZENIE ' + title + ' tydzien ' + week + ': przychod '
      + fmt(revenue) + ', dla dystrybutora ' + fmt(share);
  }

  freeSeats(screeningId: string): string[] {
    const s = LegacyDb.SCREENINGS.get(screeningId)!;
    const free: string[] = [];
    const taken = s[6] as Set<string>;
    for (let r = 1; r <= (s[3] as number); r++) {
      for (let c = 0; c < (s[4] as number); c++) {
        const seat = String.fromCharCode('A'.charCodeAt(0) + c) + r;
        if (!taken.has(seat)) {
          free.push(seat);
        }
      }
    }
    return free;
  }
}

function fmt(value: number): string {
  return Formats.amount(value);
}
