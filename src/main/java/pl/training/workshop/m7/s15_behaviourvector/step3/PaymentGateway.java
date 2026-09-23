package pl.training.workshop.m7.s15_behaviourvector.step3;

import pl.training.workshop.shared.Money;

/** Seam dla efektu ubocznego "obciążenie karty". */
@FunctionalInterface
public interface PaymentGateway {
    boolean charge(String card, Money amount);
}
