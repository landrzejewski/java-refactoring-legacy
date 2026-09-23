package pl.training.workshop.m6.s14_adapter.step2;

import pl.training.workshop.m6.s14_adapter.PaymentResult;
import pl.training.workshop.m6.s14_adapter.XmlPayGateway;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: adapter starej bramki - tłumaczy jednostki (złote -&gt; grosze), format (XML)
 * i wynik (atrybuty status/id/code). Nie jest właścicielem bramki.
 */
public final class XmlPayAdapter implements PaymentGateway {
    private final XmlPayGateway xml;

    public XmlPayAdapter(XmlPayGateway xml) {
        this.xml = xml;
    }

    @Override
    public PaymentResult pay(String reservationId, Money amount) {
        long grosze = amount.amount().movePointRight(2).longValueExact();
        String response = xml.submit("<charge ref='" + reservationId + "' amount='" + grosze + "'/>");
        if (response.contains("status='OK'")) {
            return PaymentResult.accepted(attribute(response, "id"));
        }
        return PaymentResult.declined(attribute(response, "code"));
    }

    private static String attribute(String xml, String name) {
        int start = xml.indexOf(name + "='") + name.length() + 2;
        return xml.substring(start, xml.indexOf('\'', start));
    }
}
