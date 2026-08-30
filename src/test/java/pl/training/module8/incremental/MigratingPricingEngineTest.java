package pl.training.module8.incremental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

final class MigratingPricingEngineTest {
    private static final PriceRequest REQUEST = new PriceRequest(
            new BigDecimal("100.00"), 1, BigDecimal.ZERO);
    private static final PriceQuote LEGACY_QUOTE = new PriceQuote(
            new BigDecimal("100.00"));
    private static final PriceQuote CANDIDATE_QUOTE = new PriceQuote(
            new BigDecimal("99.00"));

    @Test
    void legacyModeCallsOnlyTheLegacyImplementation() {
        var legacyCalls = new AtomicInteger();
        var candidateCalls = new AtomicInteger();
        var events = new ArrayList<VerificationEvent>();
        PricingEngine engine = engine(
                returning(legacyCalls, LEGACY_QUOTE),
                returning(candidateCalls, CANDIDATE_QUOTE),
                events::add,
                MigrationMode.LEGACY);

        PriceQuote result = engine.quote(REQUEST);

        assertEquals(LEGACY_QUOTE, result);
        assertEquals(1, legacyCalls.get());
        assertEquals(0, candidateCalls.get());
        assertEquals(0, events.size());
    }

    @Test
    void candidateModeCallsOnlyTheCandidateImplementation() {
        var legacyCalls = new AtomicInteger();
        var candidateCalls = new AtomicInteger();
        var events = new ArrayList<VerificationEvent>();
        PricingEngine engine = engine(
                returning(legacyCalls, LEGACY_QUOTE),
                returning(candidateCalls, CANDIDATE_QUOTE),
                events::add,
                MigrationMode.CANDIDATE);

        PriceQuote result = engine.quote(REQUEST);

        assertEquals(CANDIDATE_QUOTE, result);
        assertEquals(0, legacyCalls.get());
        assertEquals(1, candidateCalls.get());
        assertEquals(0, events.size());
    }

    @Test
    void verifyModeReportsAgreementAndReturnsTheLegacyResult() {
        var legacyCalls = new AtomicInteger();
        var candidateCalls = new AtomicInteger();
        var events = new ArrayList<VerificationEvent>();
        PricingEngine engine = engine(
                returning(legacyCalls, LEGACY_QUOTE),
                returning(candidateCalls, LEGACY_QUOTE),
                events::add,
                MigrationMode.VERIFY);

        PriceQuote result = engine.quote(REQUEST);

        assertSame(LEGACY_QUOTE, result);
        assertEquals(1, legacyCalls.get());
        assertEquals(1, candidateCalls.get());
        var agreement = assertInstanceOf(
                VerificationEvent.Agreement.class, events.get(0));
        assertEquals(REQUEST, agreement.request());
        assertEquals(LEGACY_QUOTE, agreement.quote());
    }

    @Test
    void verifyModeReportsDivergenceButStillReturnsTheLegacyResult() {
        var events = new ArrayList<VerificationEvent>();
        PricingEngine engine = engine(
                request -> LEGACY_QUOTE,
                request -> CANDIDATE_QUOTE,
                events::add,
                MigrationMode.VERIFY);

        PriceQuote result = engine.quote(REQUEST);

        assertSame(LEGACY_QUOTE, result);
        var divergence = assertInstanceOf(
                VerificationEvent.Divergence.class, events.get(0));
        assertEquals(REQUEST, divergence.request());
        assertEquals(LEGACY_QUOTE, divergence.legacyQuote());
        assertEquals(CANDIDATE_QUOTE, divergence.candidateQuote());
    }

    @Test
    void candidateFailureCannotChangeTheVerifyResponse() {
        var events = new ArrayList<VerificationEvent>();
        PricingEngine engine = engine(
                request -> LEGACY_QUOTE,
                request -> {
                    throw new IllegalStateException("candidate unavailable");
                },
                events::add,
                MigrationMode.VERIFY);

        PriceQuote result = engine.quote(REQUEST);

        assertSame(LEGACY_QUOTE, result);
        var failure = assertInstanceOf(
                VerificationEvent.CandidateFailure.class, events.get(0));
        assertEquals(REQUEST, failure.request());
        assertEquals(LEGACY_QUOTE, failure.legacyQuote());
        assertEquals("java.lang.IllegalStateException",
                failure.exceptionType());
        assertEquals("candidate unavailable", failure.message());
    }

    @Test
    void jvmErrorsAreNotMaskedByVerification() {
        var candidateFailure = new AssertionError("candidate corrupted");
        PricingEngine engine = engine(
                request -> LEGACY_QUOTE,
                request -> {
                    throw candidateFailure;
                },
                VerificationReporter.ignoring(),
                MigrationMode.VERIFY);

        AssertionError result = assertThrows(
                AssertionError.class,
                () -> engine.quote(REQUEST));

        assertSame(candidateFailure, result);
    }

    @Test
    void invalidCandidateResultIsReportedAsFailure() {
        var events = new ArrayList<VerificationEvent>();
        PricingEngine engine = engine(
                request -> LEGACY_QUOTE,
                request -> null,
                events::add,
                MigrationMode.VERIFY);

        PriceQuote result = engine.quote(REQUEST);

        assertSame(LEGACY_QUOTE, result);
        var failure = assertInstanceOf(
                VerificationEvent.CandidateFailure.class, events.get(0));
        assertEquals("java.lang.NullPointerException",
                failure.exceptionType());
        assertEquals("candidate quote", failure.message());
    }

    @Test
    void reporterFailureCannotChangeTheVerifyResponse() {
        PricingEngine engine = engine(
                request -> LEGACY_QUOTE,
                request -> LEGACY_QUOTE,
                event -> {
                    throw new IllegalStateException("reporter unavailable");
                },
                MigrationMode.VERIFY);

        assertSame(LEGACY_QUOTE, engine.quote(REQUEST));
    }

    @Test
    void legacyFailureRemainsAuthoritativeAndSkipsTheCandidate() {
        var candidateCalls = new AtomicInteger();
        var events = new ArrayList<VerificationEvent>();
        var legacyFailure = new IllegalStateException("legacy unavailable");
        PricingEngine engine = engine(
                request -> {
                    throw legacyFailure;
                },
                returning(candidateCalls, CANDIDATE_QUOTE),
                events::add,
                MigrationMode.VERIFY);

        IllegalStateException result = assertThrows(
                IllegalStateException.class,
                () -> engine.quote(REQUEST));

        assertSame(legacyFailure, result);
        assertEquals(0, candidateCalls.get());
        assertEquals(0, events.size());
    }

    @Test
    void missingRequestNeverReachesEitherImplementation() {
        var legacyCalls = new AtomicInteger();
        var candidateCalls = new AtomicInteger();
        PricingEngine engine = engine(
                returning(legacyCalls, LEGACY_QUOTE),
                returning(candidateCalls, CANDIDATE_QUOTE),
                VerificationReporter.ignoring(),
                MigrationMode.VERIFY);

        NullPointerException failure = assertThrows(
                NullPointerException.class,
                () -> engine.quote(null));

        assertEquals("request", failure.getMessage());
        assertEquals(0, legacyCalls.get());
        assertEquals(0, candidateCalls.get());
    }

    @Test
    void constructorRejectsEveryMissingDependency() {
        PricingEngine validEngine = request -> LEGACY_QUOTE;
        VerificationReporter validReporter = VerificationReporter.ignoring();

        assertMissing("legacy", () -> engine(
                null, validEngine, validReporter, MigrationMode.LEGACY));
        assertMissing("candidate", () -> engine(
                validEngine, null, validReporter, MigrationMode.LEGACY));
        assertMissing("reporter", () -> engine(
                validEngine, validEngine, null, MigrationMode.LEGACY));
        assertMissing("mode", () -> engine(
                validEngine, validEngine, validReporter, null));
    }

    private static MigratingPricingEngine engine(
            PricingEngine legacy,
            PricingEngine candidate,
            VerificationReporter reporter,
            MigrationMode mode) {
        return new MigratingPricingEngine(
                legacy, candidate, reporter, mode);
    }

    private static PricingEngine returning(
            AtomicInteger calls,
            PriceQuote result) {
        return request -> {
            calls.incrementAndGet();
            return result;
        };
    }

    private static void assertMissing(String message, Runnable action) {
        NullPointerException failure = assertThrows(
                NullPointerException.class, action::run);
        assertEquals(message, failure.getMessage());
    }
}
