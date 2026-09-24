import { LegacyMailer } from './LegacyMailer.js';
import { LegacyPaymentGateway } from './LegacyPaymentGateway.js';

// "Baza danych" starego systemu - globalne, mutowalne mapy.
export class LegacyDb {
  static readonly SCREENINGS = new Map<string, unknown[]>();
  static readonly BOOKINGS = new Map<string, unknown[]>();
  static readonly LOYALTY = new Map<string, number>();
  static sequence = 1;

  private constructor() {}

  static clear(): void {
    LegacyDb.SCREENINGS.clear();
    LegacyDb.BOOKINGS.clear();
    LegacyDb.LOYALTY.clear();
    LegacyDb.sequence = 1;
    LegacyMailer.SENT.length = 0;
    LegacyPaymentGateway.CHARGES.length = 0;
  }
}
