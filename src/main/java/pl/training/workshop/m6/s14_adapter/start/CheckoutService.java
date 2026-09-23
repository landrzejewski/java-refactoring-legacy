package pl.training.workshop.m6.s14_adapter.start;

import pl.training.workshop.m6.s14_adapter.PaymentResult;
import pl.training.workshop.m6.s14_adapter.RestPayClient;
import pl.training.workshop.m6.s14_adapter.RestPayClient.ChargeRequest;
import pl.training.workshop.m6.s14_adapter.RestPayClient.RestPayException;
import pl.training.workshop.m6.s14_adapter.XmlPayGateway;
import pl.training.workshop.shared.Money;

/**
 * Start: serwis kasy mówi dwoma językami - składa XML w groszach dla starej bramki i woła
 * API REST w złotych dla nowej. Szczegóły obu integracji wymieszane z logiką kasy.
 */
public final class CheckoutService {
    private final XmlPayGateway xml;
    private final RestPayClient rest;

    public CheckoutService(XmlPayGateway xml, RestPayClient rest) {
        this.xml = xml;
        this.rest = rest;
    }

    public PaymentResult pay(String provider, String reservationId, Money amount) {
        if (provider.equals("XML")) {
            long grosze = amount.amount().movePointRight(2).longValueExact();
            String request = "<charge ref='" + reservationId + "' amount='" + grosze + "'/>";
            String response = xml.submit(request);
            if (response.contains("status='OK'")) {
                return PaymentResult.accepted(attribute(response, "id"));
            }
            return PaymentResult.declined(attribute(response, "code"));
        } else if (provider.equals("REST")) {
            try {
                return PaymentResult.accepted(
                        rest.charge(new ChargeRequest(amount.amount(), "PLN", reservationId))
                            .transactionId());
            } catch (RestPayException exception) {
                return PaymentResult.declined(exception.code());
            }
        }
        throw new IllegalArgumentException("unknown provider: " + provider);
    }

    private static String attribute(String xml, String name) {
        int start = xml.indexOf(name + "='") + name.length() + 2;
        return xml.substring(start, xml.indexOf('\'', start));
    }
}
