import { Decimal } from 'decimal.js';

enum Channel { ONLINE = 'ONLINE', BOX_OFFICE = 'BOX_OFFICE' }

/**
 * Krok 1: flaga online zastąpiona jawnymi metodami bookOnline / bookAtBoxOffice.
 * Stara metoda zostaje jako @deprecated i deleguje - klienci migrują po jednym.
 * Zostaje, bo kod JS skompilowany wcześniej (inny pakiet, bundle) woła ją po nazwie
 * i pozycji argumentów - to odpowiednik zgodności binarnej z Javy.
 */
export class TicketService {
  private static readonly GLASSES = new Decimal('3.00');
  private static readonly FEE = new Decimal('2.00');

  /**
   * @deprecated użyj {@link TicketService.bookOnline} albo {@link TicketService.bookAtBoxOffice}
   */
  book(title: string, format: string, seats: number, online: boolean, ownGlasses: boolean): string {
    return online
      ? this.bookOnline(title, format, seats, ownGlasses)
      : this.bookAtBoxOffice(title, format, seats, ownGlasses);
  }

  bookOnline(title: string, format: string, seats: number, ownGlasses: boolean): string {
    return this.bookInChannel(title, format, seats, Channel.ONLINE, ownGlasses);
  }

  bookAtBoxOffice(title: string, format: string, seats: number, ownGlasses: boolean): string {
    return this.bookInChannel(title, format, seats, Channel.BOX_OFFICE, ownGlasses);
  }

  // W Javie prywatne przeciążenie book(..., Channel, ...); TS nie ma przeciążeń - osobna nazwa.
  private bookInChannel(title: string, format: string, seats: number, channel: Channel, ownGlasses: boolean): string {
    let base: Decimal;
    switch (format) {
      case 'IMAX': base = new Decimal('40.00'); break;
      case '3D': base = new Decimal('32.00'); break;
      default: base = new Decimal('25.00');
    }
    const count = new Decimal(seats);
    let total = base.times(count);
    if (format === '3D' && !ownGlasses) {
      total = total.plus(TicketService.GLASSES.times(count));
    }
    if (channel === Channel.ONLINE) {
      total = total.plus(TicketService.FEE.times(count));
    }
    return `${title} ${format} x${seats}${channel === Channel.ONLINE ? ' online' : ' kasa'}: ${total.toFixed(2)}`;
  }
}
