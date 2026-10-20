import type { Decimal } from 'decimal.js';
import type { Parcel } from './Parcel.js';
import type { ShippingMethod } from './ShippingMethod.js';

export interface DeliveryPricePolicy {
  /**
   * Returns the stable, non-null shipping method handled by this policy.
   */
  method(): ShippingMethod;

  /**
   * Returns a deterministic, non-negative amount with scale two for every
   * valid parcel, without changing the parcel or producing side effects.
   */
  priceFor(parcel: Parcel): Decimal;
}
