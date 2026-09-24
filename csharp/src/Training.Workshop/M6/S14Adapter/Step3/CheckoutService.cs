using Training.Workshop.Shared;

namespace Training.Workshop.M6.S14Adapter.Step3;

/// <summary>
/// Krok 3: logika serwisu zależy wyłącznie od IPaymentGateway. Stary konstruktor zostaje
/// jako skrót składający adaptery; trzeci dostawca to nowy adapter i wpis w mapie.
/// </summary>
public sealed class CheckoutService
{
    private readonly IReadOnlyDictionary<string, IPaymentGateway> _gateways;

    /// <summary>Dotychczasowy konstruktor jako skrót: standardowy zestaw dwóch adapterów.</summary>
    public CheckoutService(XmlPayGateway xml, RestPayClient rest)
        : this(new Dictionary<string, IPaymentGateway>
        {
            ["XML"] = new XmlPayAdapter(xml),
            ["REST"] = new RestPayAdapter(rest),
        })
    {
    }

    public CheckoutService(IReadOnlyDictionary<string, IPaymentGateway> gateways)
    {
        _gateways = new Dictionary<string, IPaymentGateway>(gateways);
    }

    public PaymentResult Pay(string provider, string reservationId, Money amount)
    {
        if (!_gateways.TryGetValue(provider, out var gateway))
        {
            throw new ArgumentException("unknown provider: " + provider);
        }
        return gateway.Pay(reservationId, amount);
    }
}
