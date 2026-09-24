namespace Training.Workshop.M6.S14Adapter;

/// <summary>
/// "Biblioteka" nowej bramki (nie zmieniamy jej): kwota w złotych, odmowa jako wyjątek.
/// Deterministyczna symulacja: powyżej 500.00 wyjątek z kodem LIMIT.
/// </summary>
public class RestPayClient
{
    public sealed record ChargeRequest(decimal Amount, string Currency, string Reference);

    public sealed record ChargeResponse(string TransactionId);

    public sealed class RestPayException : Exception
    {
        public RestPayException(string code)
            : base("payment rejected: " + code)
        {
            Code = code;
        }

        public string Code { get; }
    }

    public virtual ChargeResponse Charge(ChargeRequest request)
    {
        if (request.Amount > 500.00m)
        {
            throw new RestPayException("LIMIT");
        }
        return new ChargeResponse("T-" + request.Reference);
    }
}
