using Training.Workshop.Shared;

namespace Training.Workshop.M6.S14Adapter.Step2;

/// <summary>
/// Krok 2: Unify Interfaces with Adapter - metody przeniesione do adapterów wspólnego
/// interfejsu. Konstruktor bez zmian, wybór dostawcy jeszcze tutaj.
/// </summary>
public sealed class CheckoutService
{
    private readonly IPaymentGateway _xml;
    private readonly IPaymentGateway _rest;

    public CheckoutService(XmlPayGateway xml, RestPayClient rest)
    {
        _xml = new XmlPayAdapter(xml);
        _rest = new RestPayAdapter(rest);
    }

    public PaymentResult Pay(string provider, string reservationId, Money amount)
    {
        if (provider == "XML")
        {
            return _xml.Pay(reservationId, amount);
        }
        else if (provider == "REST")
        {
            return _rest.Pay(reservationId, amount);
        }
        throw new ArgumentException("unknown provider: " + provider);
    }
}
