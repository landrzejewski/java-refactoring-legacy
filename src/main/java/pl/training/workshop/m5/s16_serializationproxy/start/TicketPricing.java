package pl.training.workshop.m5.s16_serializationproxy.start;

import pl.training.workshop.shared.Money;

/**
 * Start: serwis final bez interfejsu. java.lang.reflect.Proxy umie opakować tylko interfejsy,
 * a proxy klasowe (CGLIB/ByteBuddy - Spring AOP, Hibernate lazy) robi PODKLASĘ, więc nie ruszy
 * klasy final ani metod final. Transakcje, audyt czy cache "z adnotacji" po cichu nie zadziałają.
 */
public final class TicketPricing {
    public Money studentPrice(Money basePrice) {
        return basePrice.minus(basePrice.percent(25));
    }
}
