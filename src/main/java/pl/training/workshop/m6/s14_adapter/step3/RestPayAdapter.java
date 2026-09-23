package pl.training.workshop.m6.s14_adapter.step3;

import pl.training.workshop.m6.s14_adapter.PaymentResult;
import pl.training.workshop.m6.s14_adapter.RestPayClient;
import pl.training.workshop.m6.s14_adapter.RestPayClient.ChargeRequest;
import pl.training.workshop.m6.s14_adapter.RestPayClient.RestPayException;
import pl.training.workshop.shared.Money;

/** Krok 3: adapter nowej bramki - odmowa zgłaszana wyjątkiem staje się wynikiem declined. */
public final class RestPayAdapter implements PaymentGateway {
    private final RestPayClient rest;

    public RestPayAdapter(RestPayClient rest) {
        this.rest = rest;
    }

    @Override
    public PaymentResult pay(String reservationId, Money amount) {
        try {
            return PaymentResult.accepted(
                    rest.charge(new ChargeRequest(amount.amount(), "PLN", reservationId))
                            .transactionId());
        } catch (RestPayException exception) {
            return PaymentResult.declined(exception.code());
        }
    }
}
