import { GroupMailer } from './notification/GroupMailer.js';
import { TicketPricing } from './pricing/TicketPricing.js';

/** Start: warstwa aplikacji - składa cennik z powiadomieniami i formatuje odpowiedź. */
export class BookingService {
  private readonly mailer = new GroupMailer();
  private readonly pricing = new TicketPricing(this.mailer);

  book(organizer: string, tickets: number, format: string): string {
    const unitPrice = BookingService.unitPrice(format);
    const total = this.pricing.total(organizer, tickets, unitPrice);
    return 'DO ZAPLATY ' + total.toFixed(2);
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
