package pl.training.workshop.m6.s14_adapter.step3;

import java.util.Map;

import pl.training.workshop.m6.s14_adapter.PaymentResult;
import pl.training.workshop.m6.s14_adapter.RestPayClient;
import pl.training.workshop.m6.s14_adapter.XmlPayGateway;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: logika serwisu zależy wyłącznie od PaymentGateway. Stary konstruktor zostaje
 * jako skrót składający adaptery; trzeci dostawca to nowy adapter i wpis w mapie.
 */
public final class CheckoutService {
    private final Map<String, PaymentGateway> gateways;

    /** Dotychczasowy konstruktor jako skrót: standardowy zestaw dwóch adapterów. */
    public CheckoutService(XmlPayGateway xml, RestPayClient rest) {
        this(Map.of("XML", new XmlPayAdapter(xml), "REST", new RestPayAdapter(rest)));
    }

    public CheckoutService(Map<String, PaymentGateway> gateways) {
        this.gateways = Map.copyOf(gateways);
    }

    public PaymentResult pay(String provider, String reservationId, Money amount) {
        PaymentGateway gateway = gateways.get(provider);
        if (gateway == null) {
            throw new IllegalArgumentException("unknown provider: " + provider);
        }
        return gateway.pay(reservationId, amount);
    }
}
