import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';
import type { PaymentResult } from '../PaymentResult.js';
import type { RestPayClient } from '../RestPayClient.js';
import { XmlPayGateway } from '../XmlPayGateway.js';
import type { PaymentGateway } from './PaymentGateway.js';
import { RestPayAdapter } from './RestPayAdapter.js';
import { XmlPayAdapter } from './XmlPayAdapter.js';

/**
 * Krok 3: logika serwisu zależy wyłącznie od PaymentGateway. Stary konstruktor zostaje
 * jako skrót składający adaptery; trzeci dostawca to nowy adapter i wpis w mapie.
 */
export class CheckoutService {
  private readonly gateways: ReadonlyMap<string, PaymentGateway>;

  /** Dotychczasowy konstruktor jako skrót: standardowy zestaw dwóch adapterów. */
  constructor(xml: XmlPayGateway, rest: RestPayClient);
  constructor(gateways: ReadonlyMap<string, PaymentGateway>);
  constructor(xmlOrGateways: XmlPayGateway | ReadonlyMap<string, PaymentGateway>, rest?: RestPayClient) {
    if (xmlOrGateways instanceof XmlPayGateway) {
      this.gateways = new Map<string, PaymentGateway>([
        ['XML', new XmlPayAdapter(xmlOrGateways)],
        ['REST', new RestPayAdapter(requireNonNull(rest, 'rest'))],
      ]);
    } else {
      this.gateways = new Map(xmlOrGateways);
    }
  }

  pay(provider: string, reservationId: string, amount: Money): PaymentResult {
    const gateway = this.gateways.get(provider);
    if (gateway === undefined) {
      throw new IllegalArgumentError(`unknown provider: ${provider}`);
    }
    return gateway.pay(reservationId, amount);
  }
}
