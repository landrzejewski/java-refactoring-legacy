import { Decimal } from 'decimal.js';

import { Glasses } from './Glasses.js';

enum Channel { ONLINE = 'ONLINE', BOX_OFFICE = 'BOX_OFFICE' }

/**
 * Krok 4 (rozwiązanie): wszyscy klienci zmigrowani, nikt nie woła już metody @deprecated,
 * więc book(..., boolean, boolean) usunięte (Safe Delete).
 * Uwaga: w bibliotece publicznej (pakiet npm) usunięcie łamie zgodność - klienci TS dostaną
 * błąd kompilacji, a kod JS skompilowany wcześniej (bez sprawdzania typów) dopiero w czasie
 * działania: TypeError "book is not a function". Tu to decyzja o końcu okresu przejściowego.
 */
export class TicketService {
  private static readonly GLASSES = new Decimal('3.00');
  private static readonly FEE = new Decimal('2.00');

  bookOnline(title: string, format: string, seats: number, glasses: Glasses): string {
    return this.bookInChannel(title, format, seats, Channel.ONLINE, glasses);
  }

  bookAtBoxOffice(title: string, format: string, seats: number, glasses: Glasses): string {
    return this.bookInChannel(title, format, seats, Channel.BOX_OFFICE, glasses);
  }

  // W Javie prywatne przeciążenie book(..., Channel, ...); TS nie ma przeciążeń - osobna nazwa.
  private bookInChannel(title: string, format: string, seats: number, channel: Channel, glasses: Glasses): string {
    let base: Decimal;
    switch (format) {
      case 'IMAX': base = new Decimal('40.00'); break;
      case '3D': base = new Decimal('32.00'); break;
      default: base = new Decimal('25.00');
    }
    const count = new Decimal(seats);
    let total = base.times(count);
    if (format === '3D' && glasses === Glasses.RENTED) {
      total = total.plus(TicketService.GLASSES.times(count));
    }
    if (channel === Channel.ONLINE) {
      total = total.plus(TicketService.FEE.times(count));
    }
    return `${title} ${format} x${seats}${channel === Channel.ONLINE ? ' online' : ' kasa'}: ${total.toFixed(2)}`;
  }
}
