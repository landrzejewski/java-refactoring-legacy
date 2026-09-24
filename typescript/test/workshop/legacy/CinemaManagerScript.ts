import { CinemaManager } from '../../../src/workshop/legacy/CinemaManager.js';
import { LegacyDb } from '../../../src/workshop/legacy/LegacyDb.js';
import { LegacyMailer } from '../../../src/workshop/legacy/LegacyMailer.js';
import { LegacyPaymentGateway } from '../../../src/workshop/legacy/LegacyPaymentGateway.js';
import { LocalDate, LocalDateTime, systemClock } from '../../../src/workshop/shared/time.js';

/**
 * Scenariusz "jednego dnia kina" używany przez golden master.
 * Zwraca pełny wektor obserwowalnego zachowania: wyniki wywołań,
 * wysłane wiadomości, operacje na bramce płatności i raporty.
 */
export function runCinemaManagerScript(): string {
  LegacyDb.clear();
  const log: string[] = [];
  const cinema = new CinemaManager();
  let now = LocalDateTime.of(2026, 3, 9, 9, 0);
  CinemaManager.clock = () => now;
  try {
    cinema.addScreening('S1', 'Diuna', 3, LocalDateTime.of(2026, 3, 10, 20, 0), 12, 10, 10);
    cinema.addScreening('S2', 'Kraina Lodu', 2, LocalDateTime.of(2026, 3, 10, 11, 0), 8, 8, 7);
    cinema.addScreening('S3', 'Amator', 1, LocalDateTime.of(2026, 3, 10, 18, 30), 10, 12, 9);

    const b1 = cinema.book('S1', 'anna@kino.pl', '600100200',
      ['A5', 'B5', 'C10'], ['N', 'S', 'E'], true, false);
    const b2 = cinema.book('S2', 'jan@kino.pl', null,
      ['A1', 'B1', 'C1', 'D7'], ['N', 'C', 'C', 'N'], false, false);
    const b3 = cinema.book('S2', 'ola@kino.pl', '600300400',
      ['E2', 'F2'], ['S', 'S'], true, true);
    const groupSeats = ['A1', 'B1', 'C1', 'D1', 'E1', 'F1', 'G1', 'H1', 'I1', 'J1'];
    const groupTypes = ['C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'N', 'N'];
    const b4 = cinema.book('S3', 'szkola@kino.pl', null, groupSeats, groupTypes, true, false);
    const b5 = cinema.book('S3', 'piotr@kino.pl', null,
      ['K9'], ['N'], false, false);
    log.push(`book: ${b1} ${b2} ${b3} ${b4} ${b5}`);
    log.push('book taken: ' + cinema.book('S1', 'x@kino.pl', null,
      ['A5'], ['N'], true, false));
    log.push('book no seat: ' + cinema.book('S1', 'x@kino.pl', null,
      ['Z99'], ['N'], true, false));
    log.push('book mismatch: ' + cinema.book('S1', 'x@kino.pl', null,
      ['A1'], [], true, false));
    log.push('book no screening: ' + cinema.book('S9', 'x@kino.pl', null,
      ['A1'], ['N'], true, false));

    log.push('pay b1: ' + cinema.pay(b1, '4111111111111111'));
    log.push('pay b1 again: ' + cinema.pay(b1, '4111111111111111'));
    log.push('pay b2 declined: ' + cinema.pay(b2, '4111111111110000'));
    log.push('pay b2: ' + cinema.pay(b2, '5555444433331111'));
    log.push('pay b4: ' + cinema.pay(b4, '4000123412341234'));

    now = LocalDateTime.of(2026, 3, 9, 9, 20);
    cinema.expireOld();
    log.push('pay b3 expired: ' + cinema.pay(b3, '4111111111111111'));

    log.push('cancel b1 early: ' + cinema.cancel(b1));
    now = LocalDateTime.of(2026, 3, 10, 10, 0);
    log.push('cancel b2 late: ' + cinema.cancel(b2));
    log.push('cancel b2 again: ' + cinema.cancel(b2));
    log.push('use b4: ' + cinema.use(b4));
    log.push('use b5 unpaid: ' + cinema.use(b5));

    log.push('points anna: ' + cinema.loyaltyPoints('anna@kino.pl'));
    log.push('points szkola: ' + cinema.loyaltyPoints('szkola@kino.pl'));
    log.push('free S1: ' + cinema.freeSeats('S1').length);
    log.push(cinema.dailyReport(LocalDate.of(2026, 3, 10)).trim());
    log.push(cinema.settlement('Amator', 1));
    log.push(cinema.settlement('Diuna', 2));
    log.push(...LegacyMailer.SENT);
    log.push(...LegacyPaymentGateway.CHARGES);
    return log.join('\n') + '\n';
  } finally {
    CinemaManager.clock = () => systemClock.now();
    LegacyDb.clear();
  }
}
