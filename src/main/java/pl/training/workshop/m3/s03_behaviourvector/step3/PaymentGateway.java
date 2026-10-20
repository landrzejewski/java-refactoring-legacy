package pl.training.workshop.m3.s03_behaviourvector.step3;

import pl.training.workshop.shared.Money;

/** Seam dla efektu ubocznego "obciążenie karty". */
@FunctionalInterface
public interface PaymentGateway {
    boolean charge(String card, Money amount);
}
