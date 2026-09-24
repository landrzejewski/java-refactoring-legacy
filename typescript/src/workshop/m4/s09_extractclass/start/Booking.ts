import { IllegalStateError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';

/**
 * Start: rezerwacja, która wie wszystko - o kliencie (imię, e-mail, telefon i ich formatowanie)
 * i o płatności (karta, status, maskowanie). Trzy powody zmiany w jednej klasie.
 */
export class Booking {
  private cardNumber = '';
  private paymentStatus = 'NEW';

  constructor(
    private readonly id: string,
    private readonly customerName: string,
    private readonly customerEmail: string,
    private readonly customerPhone: string,
    private readonly amount: Money,
  ) {}

  contact(): string {
    return this.customerName + ' <' + this.customerEmail.trim().toLowerCase() + '>, tel. '
      + this.formattedPhone();
  }

  pay(card: string): void {
    if (this.paymentStatus !== 'NEW') {
      throw new IllegalStateError('Rezerwacja ' + this.id + ' jest juz oplacona');
    }
    this.cardNumber = card.replaceAll(' ', '');
    this.paymentStatus = 'PAID';
  }

  isPaid(): boolean {
    return this.paymentStatus === 'PAID';
  }

  summary(): string {
    return 'Rezerwacja ' + this.id + '\n'
      + 'Klient: ' + this.contact() + '\n'
      + 'Kwota: ' + this.amount.toString() + '\n'
      + 'Platnosc: ' + (this.isPaid() ? 'oplacona karta ' + this.maskedCard() : 'oczekuje') + '\n';
  }

  private formattedPhone(): string {
    const digits = this.customerPhone.replace(/\D/g, '');
    const local = digits.substring(digits.length - 9);
    return local.substring(0, 3) + '-' + local.substring(3, 6) + '-' + local.substring(6);
  }

  private maskedCard(): string {
    return '**** ' + this.cardNumber.substring(this.cardNumber.length - 4);
  }
}
