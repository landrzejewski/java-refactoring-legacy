import { IllegalStateError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import { Customer } from './Customer.js';

/**
 * Krok 1: pola klienta przeniesione do {@link Customer} (Move Field x3). Logika nadal tutaj
 * i sięga po dane klienta z zewnątrz: `customer.email`, `customer.phone`.
 * Konstruktor i publiczne API bez zmian - klienci Booking nic nie zauważyli.
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
    return this.customer.name + ' <' + this.customer.email.trim().toLowerCase() + '>, tel. '
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
    const digits = this.customer.phone.replace(/\D/g, '');
    const local = digits.substring(digits.length - 9);
    return local.substring(0, 3) + '-' + local.substring(3, 6) + '-' + local.substring(6);
  }

  private maskedCard(): string {
    return '**** ' + this.cardNumber.substring(this.cardNumber.length - 4);
  }
}
