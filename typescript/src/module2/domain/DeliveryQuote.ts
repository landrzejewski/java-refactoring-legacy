import type { Decimal } from 'decimal.js';
import { ArithmeticError, IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import type { Parcel } from './Parcel.js';
import type { ShippingMethod } from './ShippingMethod.js';

export class DeliveryQuote {
  readonly customerEmail: string;
  readonly method: ShippingMethod;
  readonly parcel: Parcel;
  readonly price: Decimal;

  constructor(customerEmail: string, method: ShippingMethod, parcel: Parcel, price: Decimal) {
    requireNonNull(customerEmail, 'customerEmail');
    requireNonNull(method, 'method');
    requireNonNull(parcel, 'parcel');
    requireNonNull(price, 'price');

    if (customerEmail.trim().length === 0) {
      throw new IllegalArgumentError('Customer email must not be blank');
    }
    if (price.lt(0)) {
      throw new IllegalArgumentError('Price must not be negative');
    }
    // Odpowiednik setScale(2, RoundingMode.UNNECESSARY): więcej niż dwa miejsca po przecinku to błąd.
    if (price.decimalPlaces() > 2) {
      throw new ArithmeticError('Rounding necessary');
    }

    this.customerEmail = customerEmail;
    this.method = method;
    this.parcel = parcel;
    this.price = price;
  }
}
