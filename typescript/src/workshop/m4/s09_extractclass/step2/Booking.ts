import { IllegalStateError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import { Customer } from './Customer.js';

/**
 * Krok 2: `contact()` deleguje do {@link Customer.contactLine} - zostaje jako fasada,
 * bo jest częścią publicznego API rezerwacji. Płatność nadal wymieszana z rezerwacją.
 */
export class Booking {
  private readonly customer: Customer;
  private cardNumber = '';
  private paymentStatus = 'NEW';

  constructor(
    private readonly id: string,
    customerName: string,
    customerEmail: string,
    customerPhone: string,
    private readonly amount: Money,
  ) {
    this.customer = new Customer(customerName, customerEmail, customerPhone);
  }

  contact(): string {
    return this.customer.contactLine();
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

  private maskedCard(): string {
    return '**** ' + this.cardNumber.substring(this.cardNumber.length - 4);
  }
}
