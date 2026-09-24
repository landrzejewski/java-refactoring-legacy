import type { PointsHolder } from './PointsHolder.js';

/** Krok 2: raport zależy od roli PointsHolder - przyjmuje oba konta bez fałszywego podtypowania. */
export class LoyaltyReport {
  line(account: PointsHolder): string {
    return account.owner() + ': ' + account.points() + ' pkt';
  }
}
