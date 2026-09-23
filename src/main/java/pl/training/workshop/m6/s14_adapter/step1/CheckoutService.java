package pl.training.workshop.m6.s14_adapter.step1;

import pl.training.workshop.m6.s14_adapter.PaymentResult;
import pl.training.workshop.m6.s14_adapter.RestPayClient;
import pl.training.workshop.m6.s14_adapter.RestPayClient.ChargeRequest;
import pl.training.workshop.m6.s14_adapter.RestPayClient.RestPayException;
import pl.training.workshop.m6.s14_adapter.XmlPayGateway;
import pl.training.workshop.shared.Money;

/** Krok 1: Extract Method - dwie gałęzie jako metody o IDENTYCZNEJ sygnaturze i wyniku. */
public final class CheckoutService {
    private final XmlPayGateway xml;
    private final RestPayClient rest;

    public CheckoutService(XmlPayGateway xml, RestPayClient rest) {
        this.xml = xml;
        this.rest = rest;
    }

    public PaymentResult pay(String provider, String reservationId, Money amount) {
        if (provider.equals("XML")) {
            return payWithXml(reservationId, amount);
        } else if (provider.equals("REST")) {
            return payWithRest(reservationId, amount);
        }
        throw new IllegalArgumentException("unknown provider: " + provider);
    }

    private PaymentResult payWithXml(String reservationId, Money amount) {
        long grosze = amount.amount().movePointRight(2).longValueExact();
        String response = xml.submit("<charge ref='" + reservationId + "' amount='" + grosze + "'/>");
        if (response.contains("status='OK'")) {
            return PaymentResult.accepted(attribute(response, "id"));
        }
        return PaymentResult.declined(attribute(response, "code"));
    }

    private PaymentResult payWithRest(String reservationId, Money amount) {
        try {
            return PaymentResult.accepted(
                    rest.charge(new ChargeRequest(amount.amount(), "PLN", reservationId))
                            .transactionId());
        } catch (RestPayException exception) {
            return PaymentResult.declined(exception.code());
        }
    }

    private static String attribute(String xml, String name) {
        int start = xml.indexOf(name + "='") + name.length() + 2;
        return xml.substring(start, xml.indexOf('\'', start));
    }
}
