import { StandardTicket } from './StandardTicket.js';
import { StudentTicket } from './StudentTicket.js';
import { VipTicket } from './VipTicket.js';

/** Start: klient tworzy bilet normalny w dwóch krokach (konstruktor + setter). */
export class BoxOffice {
  describe(kind: string, seat: string, studentId: string | null): string {
    switch (kind) {
      case 'STUDENT': return new StudentTicket(seat, studentId).describe();
      case 'VIP': return new VipTicket(seat).describe();
      default: {
        const ticket = new StandardTicket();
        ticket.setSeat(seat);
        return ticket.describe();
      }
    }
  }
}
