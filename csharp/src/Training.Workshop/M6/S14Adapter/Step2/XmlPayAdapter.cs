using Training.Workshop.Shared;

namespace Training.Workshop.M6.S14Adapter.Step2;

/// <summary>
/// Krok 2: adapter starej bramki - tłumaczy jednostki (złote -&gt; grosze), format (XML)
/// i wynik (atrybuty status/id/code). Nie jest właścicielem bramki.
/// </summary>
public sealed class XmlPayAdapter : IPaymentGateway
{
    private readonly XmlPayGateway _xml;

    public XmlPayAdapter(XmlPayGateway xml)
    {
        _xml = xml;
    }

    public PaymentResult Pay(string reservationId, Money amount)
    {
        var grosze = decimal.ToInt64(amount.Amount * 100);
        var response = _xml.Submit("<charge ref='" + reservationId + "' amount='" + grosze + "'/>");
        if (response.Contains("status='OK'", StringComparison.Ordinal))
        {
            return PaymentResult.Accepted(Attribute(response, "id"));
        }
        return PaymentResult.Declined(Attribute(response, "code"));
    }

    private static string Attribute(string xml, string name)
    {
        var start = xml.IndexOf(name + "='", StringComparison.Ordinal) + name.Length + 2;
        return xml[start..xml.IndexOf('\'', start)];
    }
}
