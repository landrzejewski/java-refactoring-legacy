package pl.training.workshop.m6.s14_adapter.step2;

import pl.training.workshop.m6.s14_adapter.PaymentResult;
import pl.training.workshop.m6.s14_adapter.RestPayClient;
import pl.training.workshop.m6.s14_adapter.XmlPayGateway;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: Unify Interfaces with Adapter - metody przeniesione do adapterów wspólnego
 * interfejsu. Konstruktor bez zmian, wybór dostawcy jeszcze tutaj.
 */
public final class CheckoutService {
    private final PaymentGateway xml;
    private final PaymentGateway rest;

    public CheckoutService(XmlPayGateway xml, RestPayClient rest) {
        this.xml = new XmlPayAdapter(xml);
        this.rest = new RestPayAdapter(rest);
    }

    public PaymentResult pay(String provider, String reservationId, Money amount) {
        if (provider.equals("XML")) {
            return xml.pay(reservationId, amount);
        } else if (provider.equals("REST")) {
            return rest.pay(reservationId, amount);
        }
        throw new IllegalArgumentException("unknown provider: " + provider);
    }
}
