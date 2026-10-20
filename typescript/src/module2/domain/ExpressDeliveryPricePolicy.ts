import { Decimal } from 'decimal.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import type { DeliveryPricePolicy } from './DeliveryPricePolicy.js';
import type { FuelSurcharge } from './FuelSurcharge.js';
import type { Parcel } from './Parcel.js';
import { ShippingMethod } from './ShippingMethod.js';

const BASE_PRICE = new Decimal('20.00');
const PRICE_PER_KG = new Decimal('3.00');

export class ExpressDeliveryPricePolicy implements DeliveryPricePolicy {
  private readonly fuelSurcharge: FuelSurcharge;

  constructor(fuelSurcharge: FuelSurcharge) {
    this.fuelSurcharge = requireNonNull(fuelSurcharge);
  }

  method(): ShippingMethod {
    return ShippingMethod.EXPRESS;
  }

  priceFor(parcel: Parcel): Decimal {
    const baseAmount = BASE_PRICE.plus(parcel.weightKg.times(PRICE_PER_KG));
    return this.fuelSurcharge.addTo(baseAmount);
  }
}
