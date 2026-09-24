import { RolloutPolicy } from './RolloutPolicy.js';

/** Krok 2 (bez zmian): router deleguje decyzję do jawnej polityki. */
export class CheckoutRouter {
  constructor(private readonly policy: RolloutPolicy = RolloutPolicy.current()) {}

  useNewCheckout(email: string): boolean {
    return this.policy.allows(email);
  }
}
