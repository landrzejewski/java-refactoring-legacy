import type { Decimal } from 'decimal.js';
import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import type { DeliveryPricePolicy } from './DeliveryPricePolicy.js';
import type { Parcel } from './Parcel.js';
import type { ShippingMethod } from './ShippingMethod.js';

export class DeliveryPriceCalculator {
  private readonly policies: ReadonlyMap<ShippingMethod, DeliveryPricePolicy>;

  constructor(policies: readonly DeliveryPricePolicy[]) {
    requireNonNull(policies, 'policies');

    if (policies.length === 0) {
      throw new IllegalArgumentError('At least one policy is required');
    }

    const indexedPolicies = new Map<ShippingMethod, DeliveryPricePolicy>();
    for (const policy of policies) {
      requireNonNull(policy, 'policy');
      const method = requireNonNull(policy.method(), 'policy.method()');

      if (indexedPolicies.has(method)) {
        throw new IllegalArgumentError('Duplicate policy for method: ' + method);
      }
      indexedPolicies.set(method, policy);
    }
    this.policies = indexedPolicies;
  }

  priceFor(method: ShippingMethod, parcel: Parcel): Decimal {
    requireNonNull(method, 'method');
    requireNonNull(parcel, 'parcel');

    const policy = this.policies.get(method);
    if (policy === undefined) {
      throw new IllegalArgumentError('No pricing policy for method: ' + method);
    }
    return policy.priceFor(parcel);
  }
}
