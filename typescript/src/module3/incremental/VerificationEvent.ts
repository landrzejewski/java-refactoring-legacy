import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import type { PriceQuote } from './PriceQuote.js';
import type { PriceRequest } from './PriceRequest.js';

// Odpowiednik sealed interface VerificationEvent z trzema rekordami:
// unia dyskryminowana po polu `kind`.
export type VerificationEvent = Agreement | Divergence | CandidateFailure;

export class Agreement {
  readonly kind = 'Agreement';
  readonly request: PriceRequest;
  readonly quote: PriceQuote;

  constructor(request: PriceRequest, quote: PriceQuote) {
    this.request = requireNonNull(request, 'request');
    this.quote = requireNonNull(quote, 'quote');
  }
}

export class Divergence {
  readonly kind = 'Divergence';
  readonly request: PriceRequest;
  readonly legacyQuote: PriceQuote;
  readonly candidateQuote: PriceQuote;

  constructor(request: PriceRequest, legacyQuote: PriceQuote, candidateQuote: PriceQuote) {
    this.request = requireNonNull(request, 'request');
    this.legacyQuote = requireNonNull(legacyQuote, 'legacyQuote');
    this.candidateQuote = requireNonNull(candidateQuote, 'candidateQuote');
    if (legacyQuote.equals(candidateQuote)) {
      throw new IllegalArgumentError('divergent quotes must be different');
    }
  }
}

export class CandidateFailure {
  readonly kind = 'CandidateFailure';
  readonly request: PriceRequest;
  readonly legacyQuote: PriceQuote;
  readonly exceptionType: string;
  readonly message: string;

  constructor(
    request: PriceRequest,
    legacyQuote: PriceQuote,
    exceptionType: string,
    message: string,
  ) {
    this.request = requireNonNull(request, 'request');
    this.legacyQuote = requireNonNull(legacyQuote, 'legacyQuote');
    this.exceptionType = requireNonNull(exceptionType, 'exceptionType');
    this.message = requireNonNull(message, 'message');
    if (exceptionType.trim().length === 0) {
      throw new IllegalArgumentError('exceptionType must not be blank');
    }
  }
}
