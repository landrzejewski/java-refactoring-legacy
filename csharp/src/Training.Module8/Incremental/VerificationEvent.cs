namespace Training.Module8.Incremental;

/// <summary>
/// Zamknięta hierarchia zdarzeń (odpowiednik <c>sealed interface</c> z Javy):
/// prywatny konstruktor sprawia, że jedynymi podtypami są rekordy zagnieżdżone.
/// </summary>
public abstract record VerificationEvent
{
    private VerificationEvent()
    {
    }

    public sealed record Agreement : VerificationEvent
    {
        public Agreement(PriceRequest request, PriceQuote quote)
        {
            ArgumentNullException.ThrowIfNull(request);
            ArgumentNullException.ThrowIfNull(quote);
            Request = request;
            Quote = quote;
        }

        public PriceRequest Request { get; }

        public PriceQuote Quote { get; }
    }

    public sealed record Divergence : VerificationEvent
    {
        public Divergence(
            PriceRequest request,
            PriceQuote legacyQuote,
            PriceQuote candidateQuote)
        {
            ArgumentNullException.ThrowIfNull(request);
            ArgumentNullException.ThrowIfNull(legacyQuote);
            ArgumentNullException.ThrowIfNull(candidateQuote);
            if (legacyQuote.Equals(candidateQuote))
            {
                throw new ArgumentException(
                    "divergent quotes must be different");
            }
            Request = request;
            LegacyQuote = legacyQuote;
            CandidateQuote = candidateQuote;
        }

        public PriceRequest Request { get; }

        public PriceQuote LegacyQuote { get; }

        public PriceQuote CandidateQuote { get; }
    }

    public sealed record CandidateFailure : VerificationEvent
    {
        public CandidateFailure(
            PriceRequest request,
            PriceQuote legacyQuote,
            string exceptionType,
            string message)
        {
            ArgumentNullException.ThrowIfNull(request);
            ArgumentNullException.ThrowIfNull(legacyQuote);
            ArgumentNullException.ThrowIfNull(exceptionType);
            ArgumentNullException.ThrowIfNull(message);
            if (string.IsNullOrWhiteSpace(exceptionType))
            {
                throw new ArgumentException(
                    "exceptionType must not be blank");
            }
            Request = request;
            LegacyQuote = legacyQuote;
            ExceptionType = exceptionType;
            Message = message;
        }

        public PriceRequest Request { get; }

        public PriceQuote LegacyQuote { get; }

        public string ExceptionType { get; }

        public string Message { get; }
    }
}
