import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Start: bilet studencki - zniżka zapisana nie tutaj, lecz w PriceList.priceStudent(StudentTicket). */
export class StudentTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }
}
