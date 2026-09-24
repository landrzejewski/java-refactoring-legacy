namespace Training.Workshop.M6.S14Adapter;

/// <summary>Preferowany kontrakt kina: wynik płatności niezależny od dostawcy.</summary>
public sealed record PaymentResult(bool IsAccepted, string? TransactionId, string? DeclineCode)
{
    public static PaymentResult Accepted(string transactionId)
    {
        return new PaymentResult(true, transactionId, null);
    }

    public static PaymentResult Declined(string code)
    {
        return new PaymentResult(false, null, code);
    }
}
