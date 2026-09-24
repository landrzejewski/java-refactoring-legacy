import type { Money } from '../../../shared/Money.js';
import { StandardTicket } from './StandardTicket.js';
import { StudentTicket } from './StudentTicket.js';
import { VipTicket } from './VipTicket.js';

/** Krok 2: bez zmian - klient nadal zna konkretne klasy (label() jeszcze nie ma w bazie). */
export class BoxOffice {
  label(kind: string, title: string, basePrice: Money): string {
    switch (kind) {
      case 'STUDENT': return new StudentTicket(title, basePrice).label();
      case 'VIP': return new VipTicket(title, basePrice).label();
      default: return new StandardTicket(title, basePrice).label();
    }
  }
}
