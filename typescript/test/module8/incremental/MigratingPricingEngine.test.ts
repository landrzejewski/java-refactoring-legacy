import { AssertionError } from 'node:assert';
import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { IllegalStateError, NullPointerError } from '../../../src/shared/errors.js';
import { MigratingPricingEngine } from '../../../src/module8/incremental/MigratingPricingEngine.js';
import { MigrationMode } from '../../../src/module8/incremental/MigrationMode.js';
import { PriceQuote } from '../../../src/module8/incremental/PriceQuote.js';
import { PriceRequest } from '../../../src/module8/incremental/PriceRequest.js';
import type { PricingEngine } from '../../../src/module8/incremental/PricingEngine.js';
import {
  Agreement,
  CandidateFailure,
  Divergence,
  type VerificationEvent,
} from '../../../src/module8/incremental/VerificationEvent.js';
import { VerificationReporter } from '../../../src/module8/incremental/VerificationReporter.js';

// Odpowiednik AtomicInteger z testu Javy.
interface Counter {
  value: number;
}

const REQUEST = new PriceRequest(new Decimal('100.00'), 1, new Decimal(0));
const LEGACY_QUOTE = new PriceQuote(new Decimal('100.00'));
const CANDIDATE_QUOTE = new PriceQuote(new Decimal('99.00'));

function engine(
  legacy: PricingEngine,
  candidate: PricingEngine,
  reporter: VerificationReporter,
  mode: MigrationMode,
): MigratingPricingEngine {
  return new MigratingPricingEngine(legacy, candidate, reporter, mode);
}

function returning(calls: Counter, result: PriceQuote): PricingEngine {
  return {
    quote: () => {
      calls.value++;
      return result;
    },
  };
}

function constant(result: PriceQuote): PricingEngine {
  return { quote: () => result };
}

function collecting(events: VerificationEvent[]): VerificationReporter {
  return { report: (event) => events.push(event) };
}

function thrownBy(action: () => unknown): unknown {
  try {
    action();
  } catch (error) {
    return error;
  }
  throw new Error('expected an exception');
}

function assertMissing(message: string, action: () => unknown): void {
  const failure = thrownBy(action);
  expect(failure).toBeInstanceOf(NullPointerError);
  expect((failure as Error).message).toBe(message);
}

describe('MigratingPricingEngineTest', () => {
  it('legacyModeCallsOnlyTheLegacyImplementation', () => {
    const legacyCalls = { value: 0 };
    const candidateCalls = { value: 0 };
    const events: VerificationEvent[] = [];
    const pricingEngine: PricingEngine = engine(
      returning(legacyCalls, LEGACY_QUOTE),
      returning(candidateCalls, CANDIDATE_QUOTE),
      collecting(events),
      MigrationMode.LEGACY,
    );

    const result = pricingEngine.quote(REQUEST);

    expect(result.equals(LEGACY_QUOTE)).toBe(true);
    expect(legacyCalls.value).toBe(1);
    expect(candidateCalls.value).toBe(0);
    expect(events.length).toBe(0);
  });

  it('candidateModeCallsOnlyTheCandidateImplementation', () => {
    const legacyCalls = { value: 0 };
    const candidateCalls = { value: 0 };
    const events: VerificationEvent[] = [];
    const pricingEngine: PricingEngine = engine(
      returning(legacyCalls, LEGACY_QUOTE),
      returning(candidateCalls, CANDIDATE_QUOTE),
      collecting(events),
      MigrationMode.CANDIDATE,
    );

    const result = pricingEngine.quote(REQUEST);

    expect(result.equals(CANDIDATE_QUOTE)).toBe(true);
    expect(legacyCalls.value).toBe(0);
    expect(candidateCalls.value).toBe(1);
    expect(events.length).toBe(0);
  });

  it('verifyModeReportsAgreementAndReturnsTheLegacyResult', () => {
    const legacyCalls = { value: 0 };
    const candidateCalls = { value: 0 };
    const events: VerificationEvent[] = [];
    const pricingEngine: PricingEngine = engine(
      returning(legacyCalls, LEGACY_QUOTE),
      returning(candidateCalls, LEGACY_QUOTE),
      collecting(events),
      MigrationMode.VERIFY,
    );

    const result = pricingEngine.quote(REQUEST);

    expect(result).toBe(LEGACY_QUOTE);
    expect(legacyCalls.value).toBe(1);
    expect(candidateCalls.value).toBe(1);
    const agreement = events[0];
    expect(agreement).toBeInstanceOf(Agreement);
    if (!(agreement instanceof Agreement)) return;
    expect(agreement.request.equals(REQUEST)).toBe(true);
    expect(agreement.quote.equals(LEGACY_QUOTE)).toBe(true);
  });

  it('verifyModeReportsDivergenceButStillReturnsTheLegacyResult', () => {
    const events: VerificationEvent[] = [];
    const pricingEngine: PricingEngine = engine(
      constant(LEGACY_QUOTE),
      constant(CANDIDATE_QUOTE),
      collecting(events),
      MigrationMode.VERIFY,
    );

    const result = pricingEngine.quote(REQUEST);

    expect(result).toBe(LEGACY_QUOTE);
    const divergence = events[0];
    expect(divergence).toBeInstanceOf(Divergence);
    if (!(divergence instanceof Divergence)) return;
    expect(divergence.request.equals(REQUEST)).toBe(true);
    expect(divergence.legacyQuote.equals(LEGACY_QUOTE)).toBe(true);
    expect(divergence.candidateQuote.equals(CANDIDATE_QUOTE)).toBe(true);
  });

  it('candidateFailureCannotChangeTheVerifyResponse', () => {
    const events: VerificationEvent[] = [];
    const pricingEngine: PricingEngine = engine(
      constant(LEGACY_QUOTE),
      {
        quote: () => {
          throw new IllegalStateError('candidate unavailable');
        },
      },
      collecting(events),
      MigrationMode.VERIFY,
    );

    const result = pricingEngine.quote(REQUEST);

    expect(result).toBe(LEGACY_QUOTE);
    const failure = events[0];
    expect(failure).toBeInstanceOf(CandidateFailure);
    if (!(failure instanceof CandidateFailure)) return;
    expect(failure.request.equals(REQUEST)).toBe(true);
    expect(failure.legacyQuote.equals(LEGACY_QUOTE)).toBe(true);
    expect(failure.exceptionType).toBe('IllegalStateError');
    expect(failure.message).toBe('candidate unavailable');
  });

  it('jvmErrorsAreNotMaskedByVerification', () => {
    const candidateFailure = new AssertionError({ message: 'candidate corrupted' });
    const pricingEngine: PricingEngine = engine(
      constant(LEGACY_QUOTE),
      {
        quote: () => {
          throw candidateFailure;
        },
      },
      VerificationReporter.ignoring(),
      MigrationMode.VERIFY,
    );

    const result = thrownBy(() => pricingEngine.quote(REQUEST));

    expect(result).toBeInstanceOf(AssertionError);
    expect(result).toBe(candidateFailure);
  });

  it('invalidCandidateResultIsReportedAsFailure', () => {
    const events: VerificationEvent[] = [];
    const pricingEngine: PricingEngine = engine(
      constant(LEGACY_QUOTE),
      { quote: () => null as unknown as PriceQuote },
      collecting(events),
      MigrationMode.VERIFY,
    );

    const result = pricingEngine.quote(REQUEST);

    expect(result).toBe(LEGACY_QUOTE);
    const failure = events[0];
    expect(failure).toBeInstanceOf(CandidateFailure);
    if (!(failure instanceof CandidateFailure)) return;
    expect(failure.exceptionType).toBe('NullPointerError');
    expect(failure.message).toBe('candidate quote');
  });

  it('reporterFailureCannotChangeTheVerifyResponse', () => {
    const pricingEngine: PricingEngine = engine(
      constant(LEGACY_QUOTE),
      constant(LEGACY_QUOTE),
      {
        report: () => {
          throw new IllegalStateError('reporter unavailable');
        },
      },
      MigrationMode.VERIFY,
    );

    expect(pricingEngine.quote(REQUEST)).toBe(LEGACY_QUOTE);
  });

  it('legacyFailureRemainsAuthoritativeAndSkipsTheCandidate', () => {
    const candidateCalls = { value: 0 };
    const events: VerificationEvent[] = [];
    const legacyFailure = new IllegalStateError('legacy unavailable');
    const pricingEngine: PricingEngine = engine(
      {
        quote: () => {
          throw legacyFailure;
        },
      },
      returning(candidateCalls, CANDIDATE_QUOTE),
      collecting(events),
      MigrationMode.VERIFY,
    );

    const result = thrownBy(() => pricingEngine.quote(REQUEST));

    expect(result).toBeInstanceOf(IllegalStateError);
    expect(result).toBe(legacyFailure);
    expect(candidateCalls.value).toBe(0);
    expect(events.length).toBe(0);
  });

  it('missingRequestNeverReachesEitherImplementation', () => {
    const legacyCalls = { value: 0 };
    const candidateCalls = { value: 0 };
    const pricingEngine: PricingEngine = engine(
      returning(legacyCalls, LEGACY_QUOTE),
      returning(candidateCalls, CANDIDATE_QUOTE),
      VerificationReporter.ignoring(),
      MigrationMode.VERIFY,
    );

    assertMissing('request', () => pricingEngine.quote(null as unknown as PriceRequest));
    expect(legacyCalls.value).toBe(0);
    expect(candidateCalls.value).toBe(0);
  });

  it('constructorRejectsEveryMissingDependency', () => {
    const validEngine = constant(LEGACY_QUOTE);
    const validReporter = VerificationReporter.ignoring();

    assertMissing('legacy', () =>
      engine(null as unknown as PricingEngine, validEngine, validReporter, MigrationMode.LEGACY),
    );
    assertMissing('candidate', () =>
      engine(validEngine, null as unknown as PricingEngine, validReporter, MigrationMode.LEGACY),
    );
    assertMissing('reporter', () =>
      engine(validEngine, validEngine, null as unknown as VerificationReporter, MigrationMode.LEGACY),
    );
    assertMissing('mode', () =>
      engine(validEngine, validEngine, validReporter, null as unknown as MigrationMode),
    );
  });
});
