package pl.training.module8.incremental;

import java.util.Objects;

public sealed interface VerificationEvent {
    record Agreement(
            PriceRequest request,
            PriceQuote quote) implements VerificationEvent {
        public Agreement {
            Objects.requireNonNull(request, "request");
            Objects.requireNonNull(quote, "quote");
        }
    }

    record Divergence(
            PriceRequest request,
            PriceQuote legacyQuote,
            PriceQuote candidateQuote) implements VerificationEvent {
        public Divergence {
            Objects.requireNonNull(request, "request");
            Objects.requireNonNull(legacyQuote, "legacyQuote");
            Objects.requireNonNull(candidateQuote, "candidateQuote");
            if (legacyQuote.equals(candidateQuote)) {
                throw new IllegalArgumentException(
                        "divergent quotes must be different");
            }
        }
    }

    record CandidateFailure(
            PriceRequest request,
            PriceQuote legacyQuote,
            String exceptionType,
            String message) implements VerificationEvent {
        public CandidateFailure {
            Objects.requireNonNull(request, "request");
            Objects.requireNonNull(legacyQuote, "legacyQuote");
            Objects.requireNonNull(exceptionType, "exceptionType");
            Objects.requireNonNull(message, "message");
            if (exceptionType.isBlank()) {
                throw new IllegalArgumentException(
                        "exceptionType must not be blank");
            }
        }
    }
}
