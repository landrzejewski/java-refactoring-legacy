using Training.Workshop.Shared;

namespace Training.Workshop.M6.S14Adapter.Start;

/// <summary>
/// Start: serwis kasy mówi dwoma językami - składa XML w groszach dla starej bramki i woła
/// API REST w złotych dla nowej. Szczegóły obu integracji wymieszane z logiką kasy.
/// </summary>
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
            var grosze = decimal.ToInt64(amount.Amount * 100);
            var request = "<charge ref='" + reservationId + "' amount='" + grosze + "'/>";
            var response = _xml.Submit(request);
            if (response.Contains("status='OK'", StringComparison.Ordinal))
            {
                return PaymentResult.Accepted(Attribute(response, "id"));
            }
            return PaymentResult.Declined(Attribute(response, "code"));
        }
        else if (provider == "REST")
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
        throw new ArgumentException("unknown provider: " + provider);
    }

    private static string Attribute(string xml, string name)
    {
        var start = xml.IndexOf(name + "='", StringComparison.Ordinal) + name.Length + 2;
        return xml[start..xml.IndexOf('\'', start)];
    }
}
