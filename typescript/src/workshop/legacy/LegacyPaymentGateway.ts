// Bramka płatności starego systemu. Karty kończące się na 0000 są odrzucane.
export class LegacyPaymentGateway {
  static readonly CHARGES: string[] = [];

  private constructor() {}

  static charge(cardNumber: string | null, amount: number): boolean {
    if (cardNumber === null || cardNumber.endsWith('0000')) {
      LegacyPaymentGateway.CHARGES.push(`DECLINED ${cardNumber} ${amount.toFixed(2)}`);
      return false;
    }
    LegacyPaymentGateway.CHARGES.push(`CHARGED ${cardNumber} ${amount.toFixed(2)}`);
    return true;
  }

  static refund(cardNumber: string | null, amount: number): void {
    LegacyPaymentGateway.CHARGES.push(`REFUND ${cardNumber} ${amount.toFixed(2)}`);
  }
}
