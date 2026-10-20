import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { Money } from '../../shared/Money.js';
import type { LocalDateTime } from '../../shared/time.js';

/** Stabilny kontrakt sceny: prośba o zwrot za bilet - kwota, początek seansu, moment prośby. */
export class RefundRequest {
  readonly amount: Money;
  readonly screeningStart: LocalDateTime;
  readonly requestedAt: LocalDateTime;

  constructor(amount: Money, screeningStart: LocalDateTime, requestedAt: LocalDateTime) {
    this.amount = requireNonNull(amount, 'amount');
    this.screeningStart = requireNonNull(screeningStart, 'screeningStart');
    this.requestedAt = requireNonNull(requestedAt, 'requestedAt');
  }
}
