import { Duration, LocalDate, LocalDateTime, systemClock } from '../../../shared/time.js';
import { LegacyDb } from './LegacyDb.js';
import { LegacyMailer } from './LegacyMailer.js';
import { LegacyPaymentGateway } from './LegacyPaymentGateway.js';
import { PricingService } from './PricingService.js';

/**
 * Krok 1: Extract Class PricingService - pierwszy pionowy wycinek kampanii.
 * Cennik (ceny formatów, zniżki, poranek, VIP, okulary, rabat grupowy, opłata online)
 * ma teraz jednego właściciela. PricingService nie zna układu unknown[] - dostaje wartości.
 * Arytmetyka number (double) przeniesiona dosłownie, więc kwoty są identyczne co do bitu.
 */
export class CinemaManager {
  // format: 1 = 2D, 2 = 3D, 3 = IMAX
  // status: 0 = NEW, 1 = PAID, 2 = USED, 3 = EXPIRED, 4 = CANCELLED
  // typ biletu: N = normalny, S = student, E = senior, C = dziecko

  /** Hak dla testów dodany "na chwilę" w 2019 roku. */
  static clock: () => LocalDateTime = () => systemClock.now();

  private readonly pricing = new PricingService();

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
          const id = 'B' + (LegacyDb.sequence++);
          for (const seat of seats) {
            taken.add(seat);
          }
          LegacyDb.BOOKINGS.set(id, [screeningId, email, phone,
            seats, types, web, total, 0, CinemaManager.clock(), null, sum]);
          LegacyMailer.send(email, 'Rezerwacja ' + id,
            'Film: ' + (s[0] as string) + ', miejsca: ' + seats.join(',')
              + ', do zaplaty: ' + fmt(total));
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
    const b = LegacyDb.BOOKINGS.get(bookingId);
    if (b == null) {
      return 'ERROR: no booking';
    }
    const status = b[7] as number;
    if (status === 1) {
      return 'ERROR: already paid';
    } else if (status === 2) {
      return 'ERROR: already used';
    } else if (status === 3) {
      return 'ERROR: expired';
    } else if (status === 4) {
      return 'ERROR: cancelled';
    }
    if (!LegacyPaymentGateway.charge(card, b[6] as number)) {
      LegacyMailer.send(b[1] as string, 'Platnosc odrzucona', 'Rezerwacja ' + bookingId);
      return 'ERROR: payment declined';
    }
    b[7] = 1;
    b[9] = card;
    const email = b[1] as string;
    const points = Math.trunc((b[10] as number) / 10);
    LegacyDb.LOYALTY.set(email, (LegacyDb.LOYALTY.get(email) ?? 0) + points);
    LegacyMailer.send(email, 'Bilety ' + bookingId, 'Oplacono ' + fmt(b[6] as number)
      + ', punkty: +' + points);
    if (b[2] != null) {
      LegacyMailer.sms(b[2] as string, 'CineLegacy: bilety ' + bookingId + ' oplacone');
    }
    return 'OK';
  }

  cancel(bookingId: string): string {
    const b = LegacyDb.BOOKINGS.get(bookingId);
    if (b == null) {
      return 'ERROR: no booking';
    }
    const status = b[7] as number;
    if (status === 2 || status === 3 || status === 4) {
      return 'ERROR: cannot cancel';
    }
    const s = LegacyDb.SCREENINGS.get(b[0] as string)!;
    const taken = s[6] as Set<string>;
    for (const seat of b[3] as string[]) {
      taken.delete(seat);
    }
    b[7] = 4;
    let refund = 0;
    if (status === 1) {
      const now = CinemaManager.clock();
      const start = s[2] as LocalDateTime;
      const tickets = b[10] as number;
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
        LegacyPaymentGateway.refund(b[9] as string | null, refund);
      }
      const email = b[1] as string;
      const points = Math.trunc(tickets / 10);
      LegacyDb.LOYALTY.set(email, Math.max(0, (LegacyDb.LOYALTY.get(email) ?? 0) - points));
    }
    LegacyMailer.send(b[1] as string, 'Anulowano ' + bookingId, 'Zwrot: ' + fmt(refund));
    return 'REFUND ' + fmt(refund);
  }

  expireOld(): void {
    const now = CinemaManager.clock();
    for (const [key, b] of LegacyDb.BOOKINGS) {
      if ((b[7] as number) === 0
        && Duration.between(b[8] as LocalDateTime, now).toMinutes() >= 15) {
        b[7] = 3;
        const s = LegacyDb.SCREENINGS.get(b[0] as string)!;
        const taken = s[6] as Set<string>;
        for (const seat of b[3] as string[]) {
          taken.delete(seat);
        }
        LegacyMailer.send(b[1] as string, 'Rezerwacja wygasla', key);
      }
    }
  }

  use(bookingId: string): string {
    const b = LegacyDb.BOOKINGS.get(bookingId);
    if (b == null) {
      return 'ERROR: no booking';
    }
    if ((b[7] as number) !== 1) {
      return 'ERROR: not paid';
    }
    b[7] = 2;
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
    for (const b of LegacyDb.BOOKINGS.values()) {
      const status = b[7] as number;
      if (status !== 1 && status !== 2) {
        continue;
      }
      const s = LegacyDb.SCREENINGS.get(b[0] as string)!;
      if (!(s[2] as LocalDateTime).toLocalDate().equals(day)) {
        continue;
      }
      let row = byTitle.get(s[0] as string);
      if (row === undefined) {
        row = [0, 0];
        byTitle.set(s[0] as string, row);
      }
      row[0] = row[0]! + (b[3] as string[]).length;
      row[1] = row[1]! + (b[10] as number);
      fees = fees + ((b[6] as number) - (b[10] as number));
      tickets = tickets + (b[3] as string[]).length;
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
    for (const b of LegacyDb.BOOKINGS.values()) {
      const status = b[7] as number;
      const s = LegacyDb.SCREENINGS.get(b[0] as string)!;
      if ((status === 1 || status === 2) && s[0] === title) {
        revenue = revenue + (b[10] as number);
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
  return value.toFixed(2);
}
