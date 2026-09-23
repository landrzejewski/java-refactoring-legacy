package pl.training.workshop.m6.s14_adapter.step2;

import pl.training.workshop.m6.s14_adapter.PaymentResult;
import pl.training.workshop.shared.Money;

/** Krok 2: preferowany interfejs kina - kwota jako Money, odmowa jako wynik, nie wyjątek. */
public interface PaymentGateway {
    PaymentResult pay(String reservationId, Money amount);
}
