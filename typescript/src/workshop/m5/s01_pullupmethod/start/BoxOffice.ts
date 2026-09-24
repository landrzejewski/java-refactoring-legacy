import type { Money } from '../../../shared/Money.js';
import { StandardTicket } from './StandardTicket.js';
import { StudentTicket } from './StudentTicket.js';
import { VipTicket } from './VipTicket.js';

/** Start: klient musi znać konkretną klasę, bo `label()` nie istnieje w typie bazowym. */
export class BoxOffice {
  label(kind: string, title: string, basePrice: Money): string {
    switch (kind) {
      case 'STUDENT': return new StudentTicket(title, basePrice).label();
      case 'VIP': return new VipTicket(title, basePrice).label();
      default: return new StandardTicket(title, basePrice).label();
    }
  }
}
