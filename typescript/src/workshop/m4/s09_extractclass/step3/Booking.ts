import type { Money } from '../../../shared/Money.js';
import { Customer } from './Customer.js';
import { Payment } from './Payment.js';

/**
 * Krok 3 (rozwiązanie): Booking składa rezerwację z {@link Customer} i {@link Payment}.
 * Publiczne API (konstruktor, contact, pay, isPaid, summary) bez zmian - to fasada.
 */
export class Booking {
  private readonly customer: Customer;
  private readonly payment = new Payment();

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
    this.payment.payWith(card, this.id);
  }

  isPaid(): boolean {
    return this.payment.isPaid();
  }

  summary(): string {
    return 'Rezerwacja ' + this.id + '\n'
      + 'Klient: ' + this.contact() + '\n'
      + 'Kwota: ' + this.amount.toString() + '\n'
      + 'Platnosc: ' + this.payment.description() + '\n';
  }
}
