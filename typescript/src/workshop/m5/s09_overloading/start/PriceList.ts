import type { Money } from '../../../shared/Money.js';
import type { StudentTicket } from './StudentTicket.js';
import type { Ticket } from './Ticket.js';

/**
 * Start: pułapka 1 - wariant ceny wybiera PISZĄCY KOD według typu deklarowanego argumentu
 * (w Javie: przeciążenia price(Ticket)/price(StudentTicket) wybierane przez kompilator; w TS
 * przeciążenia to osobne nazwy). Dopóki klient miał StudentTicket[], wołał priceStudent - działało.
 * Po przejściu na Ticket[] naturalnym wyborem jest price(ticket) - student płaci pełną cenę.
 */
export class PriceList {
  price(ticket: Ticket): Money {
    return ticket.basePrice();
  }

  priceStudent(ticket: StudentTicket): Money {
    return ticket.basePrice().minus(ticket.basePrice().percent(25));
  }
}
