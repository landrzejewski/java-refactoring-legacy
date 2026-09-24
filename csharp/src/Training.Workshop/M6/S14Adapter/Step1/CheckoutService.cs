using Training.Workshop.Shared;

namespace Training.Workshop.M6.S14Adapter.Step1;

/// <summary>Krok 1: Extract Method - dwie gałęzie jako metody o IDENTYCZNEJ sygnaturze i wyniku.</summary>
public sealed class CheckoutService
{
    private readonly XmlPayGateway _xml;
    private readonly RestPayClient _rest;

    public CheckoutService(XmlPayGateway xml, RestPayClient rest)
    {
        _xml = xml;
        _rest = rest;
    }

    public PaymentResult Pay(string provider, string reservationId, Money amount)
    {
        if (provider == "XML")
        {
            return PayWithXml(reservationId, amount);
        }
        else if (provider == "REST")
        {
            return PayWithRest(reservationId, amount);
        }
        throw new ArgumentException("unknown provider: " + provider);
    }

    private PaymentResult PayWithXml(string reservationId, Money amount)
    {
        var grosze = decimal.ToInt64(amount.Amount * 100);
        var response = _xml.Submit("<charge ref='" + reservationId + "' amount='" + grosze + "'/>");
        if (response.Contains("status='OK'", StringComparison.Ordinal))
        {
            return PaymentResult.Accepted(Attribute(response, "id"));
        }
        return PaymentResult.Declined(Attribute(response, "code"));
    }

    private PaymentResult PayWithRest(string reservationId, Money amount)
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

    private static string Attribute(string xml, string name)
    {
        var start = xml.IndexOf(name + "='", StringComparison.Ordinal) + name.Length + 2;
        return xml[start..xml.IndexOf('\'', start)];
    }
}
