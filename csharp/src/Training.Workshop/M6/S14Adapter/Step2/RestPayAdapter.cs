using Training.Workshop.Shared;

namespace Training.Workshop.M6.S14Adapter.Step2;

/// <summary>Krok 2: adapter nowej bramki - odmowa zgłaszana wyjątkiem staje się wynikiem Declined.</summary>
public sealed class RestPayAdapter : IPaymentGateway
{
    private readonly RestPayClient _rest;

    public RestPayAdapter(RestPayClient rest)
    {
        _rest = rest;
    }

    public PaymentResult Pay(string reservationId, Money amount)
    {
        try
        {
            return PaymentResult.Accepted(
                _rest.Charge(new RestPayClient.ChargeRequest(amount.Amount, "PLN", reservationId))
                    .TransactionId);
        }
        catch (RestPayClient.RestPayException exception)
        {
            return PaymentResult.Declined(exception.Code);
        }
    }
}
