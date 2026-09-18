import { AssertionError } from 'node:assert';
import { assertNever } from '../../shared/assertNever.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import { MigrationMode } from './MigrationMode.js';
import type { PriceQuote } from './PriceQuote.js';
import type { PriceRequest } from './PriceRequest.js';
import type { PricingEngine } from './PricingEngine.js';
import { Agreement, CandidateFailure, Divergence, type VerificationEvent } from './VerificationEvent.js';
import type { VerificationReporter } from './VerificationReporter.js';

export class MigratingPricingEngine implements PricingEngine {
  private readonly legacy: PricingEngine;
  private readonly candidate: PricingEngine;
  private readonly reporter: VerificationReporter;
  private readonly mode: MigrationMode;

  constructor(
    legacy: PricingEngine,
    candidate: PricingEngine,
    reporter: VerificationReporter,
    mode: MigrationMode,
  ) {
    this.legacy = requireNonNull(legacy, 'legacy');
    this.candidate = requireNonNull(candidate, 'candidate');
    this.reporter = requireNonNull(reporter, 'reporter');
    this.mode = requireNonNull(mode, 'mode');
  }

  quote(request: PriceRequest): PriceQuote {
    requireNonNull(request, 'request');
    switch (this.mode) {
      case MigrationMode.LEGACY:
        return MigratingPricingEngine.requireQuote(this.legacy.quote(request), 'legacy quote');
      case MigrationMode.VERIFY:
        return this.verify(request);
      case MigrationMode.CANDIDATE:
        return MigratingPricingEngine.requireQuote(
          this.candidate.quote(request),
          'candidate quote',
        );
      default:
        return assertNever(this.mode);
    }
  }

  private verify(request: PriceRequest): PriceQuote {
    const legacyQuote = MigratingPricingEngine.requireQuote(
      this.legacy.quote(request),
      'legacy quote',
    );
    try {
      const candidateQuote = MigratingPricingEngine.requireQuote(
        this.candidate.quote(request),
        'candidate quote',
      );
      const event: VerificationEvent = legacyQuote.equals(candidateQuote)
        ? new Agreement(request, legacyQuote)
        : new Divergence(request, legacyQuote, candidateQuote);
      this.tryToReport(event);
    } catch (failure) {
      if (!MigratingPricingEngine.isRecoverable(failure)) {
        throw failure;
      }
      this.tryToReport(
        new CandidateFailure(request, legacyQuote, failure.name, failure.message),
      );
    }
    return legacyQuote;
  }

  private tryToReport(event: VerificationEvent): void {
    try {
      this.reporter.report(event);
    } catch (failure) {
      if (!MigratingPricingEngine.isRecoverable(failure)) {
        throw failure;
      }
      // Awaria reportera nie może zastąpić wyniku legacy.
    }
  }

  /**
   * Odpowiednik rozróżnienia RuntimeException/Error z Javy: łapiemy tylko
   * zwykłe błędy aplikacyjne, a naruszeń asercji (AssertionError) i wartości,
   * które nie są obiektami Error, nie maskujemy.
   */
  private static isRecoverable(failure: unknown): failure is Error {
    return failure instanceof Error && !(failure instanceof AssertionError);
  }

  private static requireQuote(quote: PriceQuote | null | undefined, message: string): PriceQuote {
    return requireNonNull(quote, message);
  }
}
