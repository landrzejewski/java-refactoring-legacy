/** Preferowany kontrakt kina: wynik płatności niezależny od dostawcy. */
export class PaymentResult {
  constructor(
    readonly accepted: boolean,
    readonly transactionId: string | null,
    readonly declineCode: string | null,
  ) {}

  static accepted(transactionId: string): PaymentResult {
    return new PaymentResult(true, transactionId, null);
  }

  static declined(code: string): PaymentResult {
    return new PaymentResult(false, null, code);
  }
}
