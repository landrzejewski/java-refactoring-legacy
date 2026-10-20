import { GroupMailer } from './notification/GroupMailer.js';
import { TicketPricing } from './pricing/TicketPricing.js';

/** Krok 1: warstwa aplikacji przejmuje wysłanie powiadomienia na podstawie Quote. */
export class BookingService {
  private readonly mailer = new GroupMailer();
  private readonly pricing = new TicketPricing();

  book(organizer: string, tickets: number, format: string): string {
    const unitPrice = BookingService.unitPrice(format);
    const quote = this.pricing.total(tickets, unitPrice);
    if (quote.groupDiscount) {
      this.mailer.groupDiscountGranted(organizer, tickets);
    }
    return 'DO ZAPLATY ' + quote.total.toFixed(2);
  }

  sentMails(): readonly string[] {
    return this.mailer.sent();
  }

  private static unitPrice(format: string): number {
    switch (format) {
      case 'IMAX': return 40.00;
      case '3D': return 32.00;
      default: return 25.00;
    }
  }
}
