import { Money } from '../../../shared/Money.js';
import { GroupMailer } from './notification/GroupMailer.js';
import { TicketPricing } from './pricing/TicketPricing.js';

/** Krok 2: warstwa aplikacji podaje cenę jednostkową jako Money. */
export class BookingService {
  private readonly mailer = new GroupMailer();
  private readonly pricing = new TicketPricing();

  book(organizer: string, tickets: number, format: string): string {
    const unitPrice = BookingService.unitPrice(format);
    const quote = this.pricing.total(tickets, unitPrice);
    if (quote.groupDiscount) {
      this.mailer.groupDiscountGranted(organizer, tickets);
    }
    return 'DO ZAPLATY ' + quote.total.toString();
  }

  sentMails(): readonly string[] {
    return this.mailer.sent();
  }

  private static unitPrice(format: string): Money {
    switch (format) {
      case 'IMAX': return Money.of('40.00');
      case '3D': return Money.of('32.00');
      default: return Money.of('25.00');
    }
  }
}
