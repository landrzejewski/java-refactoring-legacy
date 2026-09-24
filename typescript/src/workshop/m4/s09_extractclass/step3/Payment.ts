import { IllegalStateError } from '../../../../shared/errors.js';

/**
 * Krok 3: Extract Class - płatność. Własny stan (karta, status), własne reguły
 * (jedna płatność, maskowanie karty) i własny powód zmiany (bramka płatności, PCI).
 * Nie zna Booking: id dostaje jako wartość, więc nie ma referencji zwrotnej ani cyklu.
 */
export class Payment {
  private cardNumber = '';
  private status = 'NEW';

  payWith(card: string, bookingId: string): void {
    if (this.status !== 'NEW') {
      throw new IllegalStateError('Rezerwacja ' + bookingId + ' jest juz oplacona');
    }
    this.cardNumber = card.replaceAll(' ', '');
    this.status = 'PAID';
  }

  isPaid(): boolean {
    return this.status === 'PAID';
  }

  description(): string {
    return this.isPaid() ? 'oplacona karta ' + this.maskedCard() : 'oczekuje';
  }

  private maskedCard(): string {
    return '**** ' + this.cardNumber.substring(this.cardNumber.length - 4);
  }
}
