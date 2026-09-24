import { StandardTicket } from './StandardTicket.js';
import { StudentTicket } from './StudentTicket.js';
import { VipTicket } from './VipTicket.js';

/** Krok 3: bez zmian - klient nie zauważył przeniesienia pola. */
export class BoxOffice {
  describe(kind: string, seat: string, studentId: string | null): string {
    switch (kind) {
      case 'STUDENT': return new StudentTicket(seat, studentId).describe();
      case 'VIP': return new VipTicket(seat).describe();
      default: return new StandardTicket(seat).describe();
    }
  }
}
