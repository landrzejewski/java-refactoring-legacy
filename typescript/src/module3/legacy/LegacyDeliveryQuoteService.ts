import { Decimal } from 'decimal.js';
import { assertNever } from '../../shared/assertNever.js';
import { DeliveryQuote } from '../domain/DeliveryQuote.js';
import type { Parcel } from '../domain/Parcel.js';
import { ShippingMethod } from '../domain/ShippingMethod.js';

// Wszystko w jednym miejscu: cennik, dopłata paliwowa, zapis i powiadomienie.
export class LegacyDeliveryQuoteService {
  private readonly quotes: DeliveryQuote[] = [];

  createQuote(customerEmail: string, method: ShippingMethod, parcel: Parcel): DeliveryQuote {
    let price: Decimal;
    switch (method) {
      case ShippingMethod.STANDARD: {
        const base = new Decimal('10.00')
          .plus(parcel.weightKg.times(new Decimal('2.00')));
        price = LegacyDeliveryQuoteService.money(base.plus(
          base.times(new Decimal('0.08'))));
        break;
      }
      case ShippingMethod.EXPRESS: {
        const base = new Decimal('20.00')
          .plus(parcel.weightKg.times(new Decimal('3.00')));
        price = LegacyDeliveryQuoteService.money(base.plus(
          base.times(new Decimal('0.08'))));
        break;
      }
      default:
        return assertNever(method);
    }

    const quote = new DeliveryQuote(customerEmail, method, parcel, price);
    this.quotes.push(quote);

    console.log(`Quote ready for ${customerEmail}: ${method} costs ${price.toFixed(2)}`);
    return quote;
  }

  storedQuotes(): readonly DeliveryQuote[] {
    return Object.freeze([...this.quotes]);
  }

  private static money(amount: Decimal): Decimal {
    return amount.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
