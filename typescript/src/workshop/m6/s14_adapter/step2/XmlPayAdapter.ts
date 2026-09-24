import type { Money } from '../../../shared/Money.js';
import { PaymentResult } from '../PaymentResult.js';
import type { XmlPayGateway } from '../XmlPayGateway.js';
import type { PaymentGateway } from './PaymentGateway.js';

/**
 * Krok 2: adapter starej bramki - tłumaczy jednostki (złote -> grosze), format (XML)
 * i wynik (atrybuty status/id/code). Nie jest właścicielem bramki.
 */
export class XmlPayAdapter implements PaymentGateway {
  constructor(private readonly xml: XmlPayGateway) {}

  pay(reservationId: string, amount: Money): PaymentResult {
    const grosze = amount.amount.times(100).toNumber();
    const response = this.xml.submit(`<charge ref='${reservationId}' amount='${grosze}'/>`);
    if (response.includes("status='OK'")) {
      return PaymentResult.accepted(XmlPayAdapter.attribute(response, 'id'));
    }
    return PaymentResult.declined(XmlPayAdapter.attribute(response, 'code'));
  }

  private static attribute(xml: string, name: string): string {
    const start = xml.indexOf(`${name}='`) + name.length + 2;
    return xml.substring(start, xml.indexOf("'", start));
  }
}
