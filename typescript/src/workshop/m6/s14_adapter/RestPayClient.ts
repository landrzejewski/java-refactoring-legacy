import { Decimal } from 'decimal.js';

export class ChargeRequest {
  constructor(readonly amount: Decimal, readonly currency: string, readonly reference: string) {}
}

export class ChargeResponse {
  constructor(readonly transactionId: string) {}
}

export class RestPayException extends Error {
  override name = 'RestPayException';

  constructor(readonly code: string) {
    super(`payment rejected: ${code}`);
  }
}

/**
 * "Biblioteka" nowej bramki (nie zmieniamy jej): kwota w złotych, odmowa jako wyjątek.
 * Deterministyczna symulacja: powyżej 500.00 wyjątek z kodem LIMIT.
 */
export class RestPayClient {
  charge(request: ChargeRequest): ChargeResponse {
    if (request.amount.comparedTo(new Decimal('500.00')) > 0) {
      throw new RestPayException('LIMIT');
    }
    return new ChargeResponse(`T-${request.reference}`);
  }
}
